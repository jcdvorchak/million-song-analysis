package msong.track;

import java.util.Arrays;

/**
 * FullTrack object for HDF5 Files
 * Contains all section and segment data necessary for TrackSectionAnalysis
 *
 * Created by jcdvorchak on 7/6/2016.
 */
public class FullTrack extends Track {
    private double[] sectionStart, segmentsStart, segmentsLoudnessMax, segmentsLoudnessMaxTime, segmentsLoudnessStart;
    private double[][] segmentsPitches, segmentsTimbre;

    public boolean isValid() {
        if (!super.isValid() ||
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

    public Track toThin() {
        return new Track(this.getTrackName(),this.getArtistName(),this.getYear(),this.getArtistLocation(),this.getArtistTerms(),this.getArtistTermsFreq(),this.getArtistTermsWeight(),this.getDanceability(),this.getEnergy(),this.getKeyConfidence(),this.getLoudness(),this.getSongHotttnesss(),this.getTempo(),this.getDuration(),this.getKey());
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

    public int getSectionCount() {
        return sectionStart.length+1;
    }

    public int getSegmentCount() {
        return segmentsStart.length+1;
    }

//    public String getArtistLocation() {
//        return artistLocation;
//    }
//
//    public void setArtistLocation(String artistLocation) {
//        this.artistLocation = artistLocation;
//    }

    @Override
    public String toString() {
        return (super.toString() + "\n" +
                "getSectionStart : " + Arrays.toString(this.getSectionStart()) + "\n" +
                "getSegmentsStart : " + Arrays.toString(this.getSegmentsStart()) + "\n" +
                "getSegmentsLoudnessMax : " + Arrays.toString(this.getSegmentsLoudnessMax()) + "\n" +
                "getSegmentsLoudnessMaxTime : " + Arrays.toString(this.getSegmentsLoudnessMaxTime()) + "\n" +
                "getSegmentsLoudnessStart : " + Arrays.toString(this.getSegmentsLoudnessStart()) + "\n" +
                "getSegmentsPitches : " + Arrays.deepToString(this.getSegmentsPitches()) + "\n" +
                "getSegmentsTimbre : " + Arrays.deepToString(this.getSegmentsTimbre()));
    }
}
