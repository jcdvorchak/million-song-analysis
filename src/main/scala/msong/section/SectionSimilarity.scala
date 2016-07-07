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

}
