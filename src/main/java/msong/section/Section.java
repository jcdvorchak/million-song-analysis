package msong.section;

import msong.track.Track;

import java.io.Serializable;

/**
 * Created by dvorcjc on 7/7/2016.
 */
public class Section implements Serializable {
    protected Track track;
    protected Double startTime, endTime;

    public Section() {

    }

    public Section(Track track, Double startTime, Double endTime) {
        this.track = track;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Track getTrack() { return this.track; }

    public void setTrack() { this.track = track; }

    public String getArtist() {
        return track.getArtistName();
    }

    public String getTrackName() {
        return track.getTrackName();
    }

    public Double getStartTime() {
        return startTime;
    }

    public void setStartTime(Double startTime) {
        this.startTime = startTime;
    }

    public Double getEndTime() {
        return endTime;
    }

    public void setEndTime(Double endTime) {
        this.endTime = endTime;
    }

    public Double getLength() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return ("getTrack(): ") + this.track.toString() + "\n" +
                "getStartTime(): " + getStartTime() + "\n" +
                "getEndTime(): " + getEndTime() + "\n" +
                "getLength(): " + getLength() + "\n";
    }

    public String getPrettyName() {
        return this.track.getPrettyName();
    }

    public boolean isEqual(Section otherSec) {
        if (this.getStartTime()==otherSec.getStartTime() && this.getEndTime() == otherSec.getEndTime()) {
            return true;
        } else {
            return false;
        }
    }
}
