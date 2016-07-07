package msong.section

import msong.track.Track

/**
  * Similarities between two sections of a song
  *
  * Created by jcdvorchak on 7/3/2016.
  */
class SectionSimilarity(secA: Section, secB: Section,
                        pitchRawSim: Double, timbreRawSim: Double,
                        pitchCountSim: Double, timbreCountSim: Double,
                        loudnessMaxSim: Double, loudnessMaxTimeSim: Double, loudnessStartSim: Double) extends Serializable {

  object Break extends Exception {}

  private val totalSim: Double = pitchRawSim + timbreRawSim + pitchCountSim + timbreCountSim + loudnessMaxSim + loudnessMaxTimeSim + loudnessStartSim

  /*
   * Getters, Beautiful Scala Getters
   */
  def getSecA: Section = secA

  def getSecB: Section = secB

  def getPitchRawSim: Double = pitchRawSim

  def getTimbreRawSim: Double = timbreRawSim

  def getPitchCountSim: Double = pitchCountSim

  def getTimbreCountSim: Double = timbreCountSim

  def getLoudnessMaxSim: Double = loudnessMaxSim

  def getLoudnessMaxTimeSim: Double = loudnessMaxTimeSim

  def getLoudnessStartSim: Double = loudnessStartSim

  def getTotalSim: Double = totalSim

  def getTrack: Track = secA.getTrack

  /*
 * String representation of time range between secA and secB
 */
  def getTimeRangeStr: String = {
    new StringBuilder()
      .append(readableSeconds(secA.getStartTime))
      .append("-")
      .append(readableSeconds(secA.getEndTime))
      .append(" and ")
      .append(readableSeconds(secB.getStartTime))
      .append("-")
      .append(readableSeconds(secB.getEndTime))
      .toString
  }

  /*
   * Convert a double value of seconds to MMmSSs (1m29s)
   */
  def readableSeconds(totalSeconds: Double): String = {
    val MINUTES_IN_AN_HOUR = 60
    val SECONDS_IN_A_MINUTE = 60

    val seconds = totalSeconds.toInt % SECONDS_IN_A_MINUTE
    val totalMinutes = totalSeconds.toInt / SECONDS_IN_A_MINUTE
    val minutes = totalMinutes % MINUTES_IN_AN_HOUR

    if (minutes != 0) {
      minutes + "m" + seconds + "s"
    } else {
      seconds + "s"
    }
  }


  override def toString: String = {
    new StringBuilder()
      .append(secA.getPrettyName)
      .append("\n")
      .append(getTimeRangeStr)
      .append("\n")
      .append("pitch raw: ")
      .append(pitchRawSim)
      .append("\n")
      .append("timbre raw: ")
      .append(timbreRawSim)
      .append("\n")
      .append("pitch count: ")
      .append(pitchCountSim)
      .append("\n")
      .append("timbre count: ")
      .append(timbreCountSim)
      .append("\n")
      .append("loudness max: ")
      .append(loudnessMaxSim)
      .append("\n")
      .append("max time: ")
      .append(loudnessMaxTimeSim)
      .append("\n")
      .append("loudness start: ")
      .append(loudnessStartSim)
      .toString
  }

  def toCsv: String = {
    new StringBuilder()
      .append(this.getTrack.getArtistName).append(",")
      .append(this.getTrack.getTrackName).append(",")
      .append(this.getTrack.getYear).append(",")
      .append(this.getTrack.getArtistLocation).append(",")
      .append(this.getTrack.getSongHotttnesss).append(",")
      .append(top3Genre())
      .append(readableSeconds(this.getSecA.getStartTime))
      .append("-")
      .append(readableSeconds(this.getSecA.getEndTime)).append(",")
      .append(this.getSecA.getLength).append(",")
      .append(readableSeconds(secB.getStartTime))
      .append("-")
      .append(readableSeconds(secB.getEndTime)).append(",")
      .append(this.getSecA.getLength).append(",")
      .append(pitchRawSim).append(",")
      .append(timbreRawSim).append(",")
      .append(pitchCountSim).append(",")
      .append(timbreCountSim).append(",")
      .append(loudnessMaxSim).append(",")
      .append(loudnessMaxTimeSim).append(",")
      .append(loudnessStartSim)
      .toString
  }

  def top3Genre(): String = {
    var result = new StringBuilder
    var termCount = 0
    val termArr = new Array[String](3)
    termArr(0)=""
    termArr(1)=""
    termArr(2)=""

    try {
      this.getTrack.getArtistTerms.foreach { term =>
        if (this.getTrack.getArtistTermsWeight()(termCount) > 0.9 && this.getTrack.getArtistTermsFreq()(termCount) > 0.9) {
          termArr(termCount)=term
          termCount += 1
        }
        if (termCount>=3) {
          throw Break
        }
      }
    } catch {
      case Break =>
    }

    termArr.foreach(term => result.append(term).append(","))

    result.toString
  }

}
