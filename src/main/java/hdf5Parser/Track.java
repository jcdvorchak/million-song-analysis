package hdf5Parser;

import java.util.Arrays;

/**
 * Created by jcdvorchak on 7/6/2016.
 */
public class Track {
    private String trackName, artistName, year, artistLocation;
    private String[] artistTerms;
    private double[] artistTermsFreq, artistTermsWeight;

    private double danceability, energy, keyConfidence, loudness, songHotttnesss, tempo, duration;
    private int key;
    private double[] sectionStart, segmentsStart, segmentsLoudnessMax, segmentsLoudnessMaxTime, segmentsLoudnessStart;
    private double[][] segmentsPitches, segmentsTimbre;

    public boolean isValid() {
        if (this.trackName == null ||
                this.artistName == null ||
                this.year == null ||
                this.artistLocation == null ||
                this.artistTerms == null ||
                this.artistTermsFreq == null ||
                this.artistTermsWeight == null ||
//                this.danceability == null ||
//                this.energy == null ||
//                this.keyConfidence == null ||
//                this.loudness == null ||
//                this.songHotttnesss == null ||
//                this.tempo == null ||
//                this.duration == null ||
//                this.key >= 0 ||
                this.sectionStart == null ||
                this.segmentsStart == null ||
                this.segmentsLoudnessMax == null ||
                this.segmentsLoudnessMaxTime == null ||
                this.segmentsLoudnessStart == null ||
                this.segmentsPitches == null ||
                this.segmentsTimbre == null) {
            return false;
    } else {
            return true;
        }
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String[] getArtistTerms() {
        return artistTerms;
    }

    public void setArtistTerms(String[] artistTerms) {
        this.artistTerms = artistTerms;
    }

    public double[] getArtistTermsFreq() {
        return artistTermsFreq;
    }

    public void setArtistTermsFreq(double[] artistTermsFreq) {
        this.artistTermsFreq = artistTermsFreq;
    }

    public double[] getArtistTermsWeight() {
        return artistTermsWeight;
    }

    public void setArtistTermsWeight(double[] artistTermsWeight) {
        this.artistTermsWeight = artistTermsWeight;
    }

    public double getDanceability() {
        return danceability;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public double getKeyConfidence() {
        return keyConfidence;
    }

    public void setKeyConfidence(double keyConfidence) {
        this.keyConfidence = keyConfidence;
    }

    public double getLoudness() {
        return loudness;
    }

    public void setLoudness(double loudness) {
        this.loudness = loudness;
    }

    public double getSongHotttnesss() {
        return songHotttnesss;
    }

    public void setSongHotttnesss(double songHotttness) {
        this.songHotttnesss = songHotttnesss;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double[] getSectionStart() {
        return sectionStart;
    }

    public void setSectionStart(double[] sectionStart) {
        this.sectionStart = sectionStart;
    }

    public double[] getSegmentsStart() {
        return segmentsStart;
    }

    public void setSegmentsStart(double[] segmentsStart) {
        this.segmentsStart = segmentsStart;
    }

    public double[] getSegmentsLoudnessMax() {
        return segmentsLoudnessMax;
    }

    public void setSegmentsLoudnessMax(double[] segmentsLoudnessMax) {
        this.segmentsLoudnessMax = segmentsLoudnessMax;
    }

    public double[] getSegmentsLoudnessMaxTime() {
        return segmentsLoudnessMaxTime;
    }

    public void setSegmentsLoudnessMaxTime(double[] segmentsLoudnessMaxTime) {
        this.segmentsLoudnessMaxTime = segmentsLoudnessMaxTime;
    }

    public double[] getSegmentsLoudnessStart() {
        return segmentsLoudnessStart;
    }

    public void setSegmentsLoudnessStart(double[] segmentsLoudnessStart) {
        this.segmentsLoudnessStart = segmentsLoudnessStart;
    }

    public double[][] getSegmentsPitches() {
        return segmentsPitches;
    }

    public void setSegmentsPitches(double[][] segmentsPitches) {
        this.segmentsPitches = segmentsPitches;
    }

    public double[][] getSegmentsTimbre() {
        return segmentsTimbre;
    }

    public void setSegmentsTimbre(double[][] segmentsTimbre) {
        this.segmentsTimbre = segmentsTimbre;
    }

    public String getArtistLocation() {
        return artistLocation;
    }

    public void setArtistLocation(String artistLocation) {
        this.artistLocation = artistLocation;
    }

    @Override
    public String toString() {
        return ("getTrackName : " + this.getTrackName()) + "\n" +
                "getArtistName : " + this.getArtistName() + "\n" +
                "getYear : " + this.getYear() + "\n" +
                "getArtistTerms : " + Arrays.toString(this.getArtistTerms()) + "\n" +
                "getArtistTermsFreq : " + Arrays.toString(this.getArtistTermsFreq()) + "\n" +
                "getArtistTermsWeight : " + Arrays.toString(this.getArtistTermsWeight()) + "\n" +
                "getDanceability : " + this.getDanceability() + "\n" +
                "getEnergy : " + this.getEnergy() + "\n" +
                "getKey : " + this.getKey() + "\n" +
                "getKeyConfidence : " + this.getKeyConfidence() + "\n" +
                "getLoudness : " + this.getLoudness() + "\n" +
                "getSongHotttnesss : " + this.getSongHotttnesss() + "\n" +
                "getTempo : " + this.getTempo() + "\n" +
                "getDuration : " + this.getDuration() + "\n" +
                "getSectionStart : " + Arrays.toString(this.getSectionStart()) + "\n" +
                "getSegmentsStart : " + Arrays.toString(this.getSegmentsStart()) + "\n" +
                "getSegmentsLoudnessMax : " + Arrays.toString(this.getSegmentsLoudnessMax()) + "\n" +
                "getSegmentsLoudnessMaxTime : " + Arrays.toString(this.getSegmentsLoudnessMaxTime()) + "\n" +
                "getSegmentsLoudnessStart : " + Arrays.toString(this.getSegmentsLoudnessStart()) + "\n" +
                "getSegmentsPitches : " + Arrays.deepToString(this.getSegmentsPitches()) + "\n" +
                "getSegmentsTimbre : " + Arrays.deepToString(this.getSegmentsTimbre()) + "\n" +
                "getArtistLocation : " + this.getArtistLocation();
    }

    public String getPrettyName() {
        return this.artistName + " - " + this.trackName + " (" + this.getYear() + ")";
    }
}
