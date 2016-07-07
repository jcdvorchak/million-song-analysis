package msong.track;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by dvorcjc on 7/7/2016.
 */
public class Track implements Serializable {
    private String trackName, artistName, year, artistLocation;
    private String[] artistTerms;
    private double[] artistTermsFreq, artistTermsWeight;

    private double danceability, energy, keyConfidence, loudness, songHotttnesss, tempo, duration;
    private int key;

    public Track() {

    }

    public Track(String trackName, String artistName, String year, String artistLocation, String[] artistTerms, double[] artistTermsFreq, double[] artistTermsWeight, double danceability, double energy, double keyConfidence, double loudness, double songHotttnesss, double tempo, double duration, int key) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.year = year;
        this.artistLocation = artistLocation;
        this.artistTerms = artistTerms;
        this.artistTermsFreq = artistTermsFreq;
        this.artistTermsWeight = artistTermsWeight;
        this.danceability = danceability;
        this.energy = energy;
        this.keyConfidence = keyConfidence;
        this.loudness = loudness;
        this.songHotttnesss = songHotttnesss;
        this.tempo = tempo;
        this.duration = duration;
        this.key = key;
    }

    public boolean isValid() {
        if (this.trackName == null ||
                this.artistName == null ||
                this.year == null ||
                this.artistLocation == null ||
                this.artistTerms == null ||
                this.artistTermsFreq == null ||
                this.artistTermsWeight == null
//                this.danceability == null ||
//                this.energy == null ||
//                this.keyConfidence == null ||
//                this.loudness == null ||
//                this.songHotttnesss == null ||
//                this.tempo == null ||
//                this.duration == null ||
//                this.key >= 0 ||
                ) {
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

    public String getArtistLocation() {
        return artistLocation;
    }

    public void setArtistLocation(String artistLocation) {
        this.artistLocation = artistLocation;
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


    @Override
    public String toString() {
        return ("getTrackName : " + this.getTrackName()) + "\n" +
                "getArtistName : " + this.getArtistName() + "\n" +
                "getYear : " + this.getYear() + "\n" +
                "getArtistLocation : " + this.getArtistLocation() + "\n" +
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
                "getDuration : " + this.getDuration();
    }

    public String getPrettyName() {
        return this.artistName + " - " + this.trackName + " (" + this.getYear() + ")";
    }

}
