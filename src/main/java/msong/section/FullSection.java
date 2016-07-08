package msong.section;

import java.util.Arrays;
import java.util.TreeMap;

import msong.track.Track;
import scala.collection.JavaConversions.*;

/**
 * Contains all section and segment data necessary for TrackSectionAnalysis
 *
 * Created by dvorcjc on 7/7/2016.
 */
public class FullSection extends Section {
    private int[] pitchCountArr, timbreCountArr;
    private double[] pitchRawArr, timbreRawArr;// = new Array[Double](Math.abs(segEndIndex - segStartIndex) * 12)
    private double[] loudnessMaxArr, loudnessMaxTimeArr, loudnessStartArr;// = new Array[Double](Math.abs(segEndIndex - segStartIndex))

    public FullSection() {
        super();
    }

    public FullSection(Track track, Double startTime, Double endTime, int[] pitchCountArr, int[] timbreCountArr, double[] pitchRawArr, double[] timbreRawArr, double[] loudnessMaxArr, double[] loudnessMaxTimeArr, double[] loudnessStartArr) {
        super(track, startTime, endTime);
        this.pitchCountArr = pitchCountArr;
        this.timbreCountArr = timbreCountArr;
        this.pitchRawArr = pitchRawArr;
        this.timbreRawArr = timbreRawArr;
        this.loudnessMaxArr = loudnessMaxArr;
        this.loudnessMaxTimeArr = loudnessMaxTimeArr;
        this.loudnessStartArr = loudnessStartArr;
    }

    public Section toThin() {
        return new Section(this.track, this.startTime, this.endTime);
    }

    public int[] getPitchCountArr() {
        return pitchCountArr;
    }

    public void setPitchCountArr(int[] pitchCountArr) {
        this.pitchCountArr = pitchCountArr;
    }

    public int[] getTimbreCountArr() {
        return timbreCountArr;
    }

    public void setTimbreCountArr(int[] timbreCountArr) {
        this.timbreCountArr = timbreCountArr;
    }

    public double[] getPitchRawArr() {
        return pitchRawArr;
    }

    public void setPitchRawArr(double[] pitchRawArr) {
        this.pitchRawArr = pitchRawArr;
    }

    public double[] getTimbreRawArr() {
        return timbreRawArr;
    }

    public void setTimbreRawArr(double[] timbreRawArr) {
        this.timbreRawArr = timbreRawArr;
    }

    public double[] getLoudnessMaxArr() {
        return loudnessMaxArr;
    }

    public void setLoudnessMaxArr(double[] loudnessMaxArr) {
        this.loudnessMaxArr = loudnessMaxArr;
    }

    public double[] getLoudnessMaxTimeArr() {
        return loudnessMaxTimeArr;
    }

    public void setLoudnessMaxTimeArr(double[] loudnessMaxTimeArr) {
        this.loudnessMaxTimeArr = loudnessMaxTimeArr;
    }

    public double[] getLoudnessStartArr() {
        return loudnessStartArr;
    }

    public void setLoudnessStartArr(double[] loudnessStartArr) {
        this.loudnessStartArr = loudnessStartArr;
    }

    @Override
    public String toString() {
        return super.toString() +
                "getPitchCountMap(): " + Arrays.toString(getPitchCountArr()) + "\n" +
                "getTimbreCountMap(): " + Arrays.toString(getTimbreCountArr()) + "\n" +
                "getPitchRawArr(): " + Arrays.toString(getPitchRawArr()) + "\n" +
                "getTimbreRawArr(): " + Arrays.toString(getTimbreRawArr()) + "\n" +
                "getLoudnessMaxArr(): " + Arrays.toString(getLoudnessMaxArr()) + "\n" +
                "getLoudnessMaxTimeArr(): " + Arrays.toString(getLoudnessMaxTimeArr()) + "\n" +
                "getLoudnessStartArr(): " + Arrays.toString(getLoudnessStartArr()) + "\n";
    }
}
