package analysis

/**
  * Metadata for a song section
  *
  * Created by jcdvorchak on 7/3/2016.
  */
class Section(artist: String, track: String, startTime: Double, endTime: Double) {
  val length = endTime - startTime

  def getArtist: String = artist

  def getTrack: String = track

  def getStartTime: Double = startTime

  def getEndTime: Double = endTime

//  def getConfidence: Double = confidence

  def getLength: Double = length
}
