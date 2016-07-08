package msong.hdf5Parser;

import msong.track.FullTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.StructureData;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Structure;

import java.io.IOException;
import java.util.Arrays;

/**
 * Parser for Million Song Dataset HDF5 files using NetCDF
 * Specific to the fields I required
 *
 * Created by jcdvorchak on 7/6/2016.
 */
public class MSongHDF5Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MSongHDF5Parser.class);

    public static FullTrack readHDF5File(String filename, byte[] fileContent) {
        FullTrack fullTrack = new FullTrack();
        try {
            NetcdfFile ncFile = NetcdfFile.openInMemory(filename, fileContent);
            fullTrack = parseHDF5ToTrack(ncFile);
        } catch (IOException e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        }
        return fullTrack;
    }

    public static FullTrack readHDF5File(byte[] fileContent) {
        FullTrack fullTrack = new FullTrack();
        try {
            NetcdfFile ncdfFile = NetcdfFile.openInMemory("", fileContent);
            fullTrack = parseHDF5ToTrack(ncdfFile);
            ncdfFile.close();
        } catch (IOException e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error opening file: " + e.getMessage());
        }
        return fullTrack;
    }

    public static FullTrack parseHDF5ToTrack(NetcdfFile ncdfFile) {
        FullTrack fullTrack = new FullTrack();

        StructureData metadataSongs = null;
        try {
            Structure metadataSongsStruct = (Structure) ncdfFile.findVariable("/metadata/songs");
            metadataSongs = metadataSongsStruct.getStructureIterator().next();
        } catch (Exception e) {
            LOGGER.error("Error reading /metadata/songs: " + e.getMessage());
        }
        fullTrack.setTrackName(metadataSongs.getScalarString("title"));
        fullTrack.setArtistName(metadataSongs.getScalarString("artist_name"));
        fullTrack.setArtistLocation(metadataSongs.getScalarString("artist_location"));
        fullTrack.setSongHotttnesss(metadataSongs.getScalarDouble("artist_hotttnesss"));

        try {
            char[] termChars = (char[]) ncdfFile.findVariable("/metadata/artist_terms").read().copyTo1DJavaArray();
            fullTrack.setArtistTerms(charArrToStringArr(termChars,256));
            fullTrack.setArtistTermsFreq((double[]) ncdfFile.findVariable("/metadata/artist_terms_freq").read().copyTo1DJavaArray());
            fullTrack.setArtistTermsWeight((double[]) ncdfFile.findVariable("/metadata/artist_terms_weight").read().copyTo1DJavaArray());
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
        fullTrack.setYear((Integer.toString(musicbrainzSongs.getScalarInt("year"))));

        try {
            fullTrack.setSectionStart((double[]) ncdfFile.findVariable("/analysis/sections_start").read().copyTo1DJavaArray());
            fullTrack.setSegmentsStart((double[]) ncdfFile.findVariable("/analysis/segments_start").read().copyTo1DJavaArray());
            fullTrack.setSegmentsLoudnessMax((double[]) ncdfFile.findVariable("/analysis/segments_loudness_max").read().copyTo1DJavaArray());
            fullTrack.setSegmentsLoudnessMaxTime((double[]) ncdfFile.findVariable("/analysis/segments_loudness_max_time").read().copyTo1DJavaArray());
            fullTrack.setSegmentsLoudnessStart((double[]) ncdfFile.findVariable("/analysis/segments_loudness_start").read().copyTo1DJavaArray());
            fullTrack.setSegmentsPitches((double[][]) ncdfFile.findVariable("/analysis/segments_pitches").read().copyToNDJavaArray());
            fullTrack.setSegmentsTimbre((double[][]) ncdfFile.findVariable("/analysis/segments_timbre").read().copyToNDJavaArray());
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
        fullTrack.setDanceability(analysisSongs.getScalarDouble("danceability"));
        fullTrack.setEnergy(analysisSongs.getScalarDouble("energy"));
        fullTrack.setKey(analysisSongs.getScalarInt("key"));
        fullTrack.setKeyConfidence(analysisSongs.getScalarDouble("key_confidence"));
        fullTrack.setLoudness(analysisSongs.getScalarDouble("loudness"));
        fullTrack.setDanceability(analysisSongs.getScalarDouble("danceability"));
        fullTrack.setTempo(analysisSongs.getScalarDouble("tempo"));
        fullTrack.setDuration(analysisSongs.getScalarDouble("duration"));

        return fullTrack;
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
            result[count] = new String(Arrays.copyOfRange(termChars, index, index+offset)).trim();

            index+=256;
            count++;
        }

        return result;
    }
}
