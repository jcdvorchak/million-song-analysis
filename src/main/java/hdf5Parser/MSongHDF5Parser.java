package hdf5Parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.StructureData;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Structure;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by jcdvorchak on 7/6/2016.
 */
public class MSongHDF5Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MSongHDF5Parser.class);

    public static Track readHDF5File(String filename, byte[] fileContent) {
        Track track = new Track();
        try {
            NetcdfFile ncFile = NetcdfFile.openInMemory(filename, fileContent);
            track = parseHDF5ToTrack(ncFile);
        } catch (IOException e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        }
        return track;
    }

    public static Track readHDF5File(byte[] fileContent) {
        Track track = new Track();
        try {
            NetcdfFile ncdfFile = NetcdfFile.openInMemory("", fileContent);
            track = parseHDF5ToTrack(ncdfFile);
            ncdfFile.close();
        } catch (IOException e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        }
        return track;
    }

    public static Track parseHDF5ToTrack(NetcdfFile ncdfFile) {
        Track track = new Track();

        StructureData metadataSongs = null;
        try {
            Structure metadataSongsStruct = (Structure) ncdfFile.findVariable("/metadata/songs");
            metadataSongs = metadataSongsStruct.getStructureIterator().next();
        } catch (Exception e) {
            LOGGER.error("Error reading /metadata/songs: " + e.getMessage());
        }
        track.setTrackName(metadataSongs.getScalarString("title"));
        track.setArtistName(metadataSongs.getScalarString("artist_name"));
        track.setArtistLocation(metadataSongs.getScalarString("artist_location"));
        track.setSongHotttnesss(metadataSongs.getScalarDouble("artist_hotttnesss"));

        try {
            char[] termChars = (char[]) ncdfFile.findVariable("/metadata/artist_terms").read().copyTo1DJavaArray();
            track.setArtistTerms(charArrToStringArr(termChars,256));
            track.setArtistTermsFreq((double[]) ncdfFile.findVariable("/metadata/artist_terms_freq").read().copyTo1DJavaArray());
            track.setArtistTermsWeight((double[]) ncdfFile.findVariable("/metadata/artist_terms_weight").read().copyTo1DJavaArray());
        } catch (Exception e) {
            LOGGER.error("Error reading /metadata/artist_*: " + e.getMessage());
        }

        StructureData musicbrainzSongs = null;
        try {
            Structure musicbrainzSongsStruct = (Structure) ncdfFile.findVariable("/musicbrainz/songs");
            musicbrainzSongs = musicbrainzSongsStruct.getStructureIterator().next();
        } catch (Exception e) {
            LOGGER.error("Error reading /metadata/songs: " + e.getMessage());
        }
        track.setYear((Integer.toString(musicbrainzSongs.getScalarInt("year"))));

        try {
            track.setSectionStart((double[]) ncdfFile.findVariable("/analysis/sections_start").read().copyTo1DJavaArray());
            track.setSegmentsStart((double[]) ncdfFile.findVariable("/analysis/segments_start").read().copyTo1DJavaArray());
            track.setSegmentsLoudnessMax((double[]) ncdfFile.findVariable("/analysis/segments_loudness_max").read().copyTo1DJavaArray());
            track.setSegmentsLoudnessMaxTime((double[]) ncdfFile.findVariable("/analysis/segments_loudness_max_time").read().copyTo1DJavaArray());
            track.setSegmentsLoudnessStart((double[]) ncdfFile.findVariable("/analysis/segments_loudness_start").read().copyTo1DJavaArray());
            track.setSegmentsPitches((double[][]) ncdfFile.findVariable("/analysis/segments_pitches").read().copyToNDJavaArray());
            track.setSegmentsTimbre((double[][]) ncdfFile.findVariable("/analysis/segments_timbre").read().copyToNDJavaArray());
        } catch (Exception e) {
            LOGGER.error("Error reading /analysis/sections_* or /analysis/segments_*: " + e.getMessage());
        }

        StructureData analysisSongs = null;
        try {
            Structure analysisSongsStruct = (Structure) ncdfFile.findVariable("/analysis/songs");
            analysisSongs = analysisSongsStruct.getStructureIterator().next();
        } catch (Exception e) {
            LOGGER.error("Error reading /analysis/songs/: " + e.getMessage());
        }
        track.setDanceability(analysisSongs.getScalarDouble("danceability"));
        track.setEnergy(analysisSongs.getScalarDouble("energy"));
        track.setKey(analysisSongs.getScalarInt("key"));
        track.setKeyConfidence(analysisSongs.getScalarDouble("key_confidence"));
        track.setLoudness(analysisSongs.getScalarDouble("loudness"));
        track.setDanceability(analysisSongs.getScalarDouble("danceability"));
        track.setTempo(analysisSongs.getScalarDouble("tempo"));
        track.setDuration(analysisSongs.getScalarDouble("duration"));

        return track;
    }

    /**
     * Every 256 char in the char[] is it's own string
     *
     * @param termChars
     * @return
     */
    private static String[] charArrToStringArr(char[] termChars, int offset) {
        int index = 0;
        int count = 0;
        String[] result = new String[termChars.length/offset];

        while (index < termChars.length) {
            result[count] = new String(Arrays.copyOfRange(termChars, index, index+offset));

            index+=256;
            count++;
        }

        return result;
    }
}
