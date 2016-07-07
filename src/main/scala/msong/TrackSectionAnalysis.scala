package msong

import java.util

import msong.section.{SectionSimilarity, Section, FullSection}
import msong.track.FullTrack

import scala.collection.JavaConversions._

/**
  * Created by jcdvorchak on 7/3/2016.
  */
object TrackSectionAnalysis {

  // used for breaking out loops
  object Break extends Exception {}

  var brokenFile = false

  /**
    * Convert a FullTrack into an array of FullSections
    *
    * @param track
    * @return
    */
  def trackToSections(track: FullTrack): Array[FullSection] = {
    if (!track.isValid) {
      return null
    }

    val sectionCount = track.getSectionCount
    val segmentCount = track.getSegmentCount

    val sectionArr: Array[FullSection] = new Array[FullSection](sectionCount - 1)

    val sectionSegmentStartIndices = getSectionSegmentStartIndex(track.getSectionStart, track.getSegmentsStart)
    var segStartIndex, segEndIndex: Int = 0

    val flatPitches = track.getSegmentsPitches.flatten
    val flatTimbre = track.getSegmentsTimbre.flatten
    var pitchCountMap, timbreCountMap: util.TreeMap[Double, Int] = null
    var pitchRawArr, timbreRawArr: Array[Double] = null
    var loudnessMaxArr, loudnessMaxTimeArr, loudnessStartArr: Array[Double] = null
    var segInnerIndex, vectorIndex: Int = -1
    var count = 0
    var pitchRound, timbreRound = 0.0
    for (i <- sectionSegmentStartIndices.indices) {
      // for each section
      segStartIndex = sectionSegmentStartIndices(i)
      if (i < sectionSegmentStartIndices.length - 1) {
        segEndIndex = sectionSegmentStartIndices(i + 1)
      } else {
        segEndIndex = segmentCount - 1
      }

      pitchCountMap = new util.TreeMap[Double, Int]()
      timbreCountMap = new util.TreeMap[Double, Int]()
      pitchRawArr = new Array[Double](Math.abs(segEndIndex - segStartIndex) * 12)
      timbreRawArr = new Array[Double](Math.abs(segEndIndex - segStartIndex) * 12)
      loudnessMaxArr = new Array[Double](Math.abs(segEndIndex - segStartIndex))
      loudnessMaxTimeArr = new Array[Double](Math.abs(segEndIndex - segStartIndex))
      loudnessStartArr = new Array[Double](Math.abs(segEndIndex - segStartIndex))
      segInnerIndex = -1
      vectorIndex = -1
      count = 0
      pitchRound = 0.0
      timbreRound = 0.0

      // loop from our start to the index before the next start
      for (j <- segStartIndex until segEndIndex) {
        // for each segment in this section
        loudnessMaxArr(count) = track.getSegmentsLoudnessMax()(j)
        loudnessMaxTimeArr(count) = track.getSegmentsLoudnessMaxTime()(j)
        loudnessStartArr(count) = track.getSegmentsLoudnessStart()(j)

        segInnerIndex = j * 12
        vectorIndex = count * 12
        for (l <- 0 until 12) {
          // for each pitch/timbre in this segment
          try {
            // wrapped in a try so I can break by throwing a Done exception
            if (segInnerIndex + l >= segmentCount * 12) {
              throw Break
            }

            pitchRawArr(vectorIndex + l) = flatPitches(segInnerIndex + l)
            timbreRawArr(vectorIndex + 1) = flatTimbre(segInnerIndex + 1)

            pitchRound = flatPitches(segInnerIndex + l).toString.charAt(2).asDigit.toDouble / 10
            timbreRound = flatTimbre(segInnerIndex + l).toString.charAt(2).asDigit.toDouble / 10
            if (!pitchCountMap.containsKey(pitchRound)) {
              pitchCountMap.put(pitchRound, 0)
            } else {
              pitchCountMap.put(pitchRound, pitchCountMap.get(pitchRound) + 1)
            }
            if (!timbreCountMap.containsKey(timbreRound)) {
              timbreCountMap.put(timbreRound, 0)
            } else {
              timbreCountMap.put(timbreRound, timbreCountMap.get(timbreRound) + 1)
            }
          } catch {
            case Break =>
          }
        }
        count += 1
      }

      if (i == sectionSegmentStartIndices.length - 1) {
        // last iteration
        sectionArr(i) = new FullSection(track.toThin, track.getSectionStart()(i),
          track.getSegmentsStart()(segmentCount - 2), pitchCountMap.valuesIterator.toArray, timbreCountMap.valuesIterator.toArray,
          pitchRawArr, timbreRawArr, loudnessMaxArr, loudnessMaxTimeArr, loudnessStartArr)
      } else {
        sectionArr(i) = new FullSection(track.toThin, track.getSectionStart()(i),
          track.getSectionStart()(i + 1), pitchCountMap.valuesIterator.toArray, timbreCountMap.valuesIterator.toArray,
          pitchRawArr, timbreRawArr, loudnessMaxArr, loudnessMaxTimeArr, loudnessStartArr)
      }

    }

    sectionArr
  }

  def sectionsToSimilarity(sectionA: FullSection, sectionB: FullSection): SectionSimilarity = {
    var sectionSimilarity: SectionSimilarity = null

    // only return good comps
    // check if you should even process the comp beforehand (length differences,same section, etc.)
    if (isSimilarLength(sectionA,sectionB,.8) && sectionA!=sectionB ) {

      var pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim = 0.0

      var maxSimTotal = 0.0 // save section with highest total? use as baseline? nah
      var currSimTotal = 0.0

      pitchRawSim = cosineSimilarity(sectionA.getPitchRawArr, sectionB.getPitchRawArr)
      timbreRawSim = cosineSimilarity(sectionA.getTimbreRawArr, sectionB.getTimbreRawArr)
      pitchCountSim = cosineSimilarity(sectionA.getPitchCountArr, sectionB.getPitchCountArr)
      timbreCountSim = cosineSimilarity(sectionA.getTimbreCountArr, sectionB.getTimbreCountArr)
      loudnessMaxSim = cosineSimilarity(sectionA.getLoudnessMaxArr, sectionB.getLoudnessMaxArr)
      loudnessMaxTimeSim = cosineSimilarity(sectionA.getLoudnessMaxTimeArr, sectionB.getLoudnessMaxTimeArr)
      loudnessStartSim = cosineSimilarity(sectionA.getLoudnessStartArr, sectionB.getLoudnessStartArr)

      currSimTotal = pitchRawSim + timbreRawSim + pitchCountSim + timbreCountSim + loudnessMaxSim + loudnessMaxTimeSim + loudnessStartSim
      if (currSimTotal > maxSimTotal) {
        maxSimTotal = currSimTotal
      }

      sectionSimilarity = new SectionSimilarity(sectionA.toThin, sectionB.toThin, pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim)

      if (!isMatch(sectionSimilarity,maxSimTotal)) {
        sectionSimilarity = null
      }
    }

    sectionSimilarity
  }

  /*
   * Find which segment starts a section
   * First one will skip ahead with this algo, its always 0 tho a*b time complex
   */
  private def getSectionSegmentStartIndex(sectionsStart: Array[Double], segmentsStart: Array[Double]): Array[Int] = {
    val sectionSegmentStart = new Array[Int](sectionsStart.length)

    var count = 0
    var segmentIndex = 0

    sectionsStart.foreach { sec =>
      segmentIndex = 0
      try {
        segmentsStart.foreach { seg =>
          if (sec < seg) {
            sectionSegmentStart(count) = segmentIndex
            throw Break
          }
          segmentIndex += 1
        }
      } catch {
        case Break =>
      }
      count += 1
    }

    sectionSegmentStart
  }

  /*
   * Check if the length of secA and secB are within a certain threshold of each other
   */
  def isSimilarLength(sectionA: FullSection, sectionB: FullSection, threshold: Double): Boolean = {
    var long = 0.0
    var short = 0.0
    if (sectionA.getLength > sectionB.getLength) {
      long = sectionA.getLength
      short = sectionB.getLength
    } else {
      short = sectionA.getLength
      long = sectionB.getLength
    }

    short >= long * threshold
  }

  def isMatch(secSim: SectionSimilarity, maxTot: Double): Boolean = {
    //    val maxDistance = 99.0 / 100.0
    //    secSim.getTotalSim >= maxTot * maxDistance &&
    //        secSim.getSecA.getConfidence < .5 && secSim.getSecB.getConfidence < .5 &&
    (secSim.getPitchRawSim + secSim.getTimbreRawSim) > 1 &&
      secSim.getPitchCountSim > .99 &&
      secSim.getTimbreCountSim > .99 &&
      secSim.getLoudnessMaxSim + secSim.getLoudnessMaxTimeSim + secSim.getLoudnessStartSim > 2.42 &&
//      secSim.isSimilarLength(0.8) &&
      (secSim.getSecA.getEndTime != secSim.getSecB.getStartTime) &&
      (secSim.getSecA.getStartTime != secSim.getSecB.getEndTime)

    //        currSim.isSectionConfident(0.5))
    //    var newTot = secSim.getPitchRawSim
    //    newTot += secSim.getPitchCountSim
    //    newTot += secSim.getTimbreRawSim
    //    newTot += secSim.getTimbreCountSim
    //    newTot += secSim.getLoudnessMaxSim*.5
    //    newTot += secSim.getLoudnessMaxTimeSim*.5
    //    newTot += secSim.getLoudnessStartSim*.5
  }

  def cosineSimilarity(vectorA: Array[Double], vectorB: Array[Double]): Double = {
    var dotProduct = 0.0
    var normA = 0.0
    var normB = 0.0

    var indices = vectorA.indices
    if (vectorA.length > vectorB.length) {
      indices = vectorB.indices
    }

    for (i <- indices) {
      dotProduct += vectorA(i) * vectorB(i)
      normA += Math.pow(vectorA(i), 2)
      normB += Math.pow(vectorB(i), 2)
    }

    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))
  }

  def cosineSimilarity(vectorA: Array[Int], vectorB: Array[Int]): Double = {
    var dotProduct = 0.0
    var normA = 0.0
    var normB = 0.0

    var indices = vectorA.indices
    if (vectorA.length > vectorB.length) {
      indices = vectorB.indices
    }
    for (i <- indices) {
      dotProduct += vectorA(i) * vectorB(i)
      normA += Math.pow(vectorA(i), 2)
      normB += Math.pow(vectorB(i), 2)
    }

    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))
  }

}
