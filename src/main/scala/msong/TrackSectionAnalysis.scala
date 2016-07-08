package msong

import java.util

import msong.section.{SectionSimilarity, FullSection}
import msong.track.FullTrack

import scala.collection.JavaConversions._

/**
  * Functions for finding similarities between track sections
  *
  * Created by jcdvorchak on 7/3/2016.
  */
object TrackSectionAnalysis {
  // used for breaking out loops
  object Break extends Exception {}

  var brokenFile = false

  /**
    * Convert a FullTrack into an array of FullSections
    *
    * @param track FullTrack to convert
    * @return array of FullSections
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

  /**
    * Convert two sections into a SectionSimilarity object
    *
    * @param sectionA FullSection
    * @param sectionB FullSection
    * @return SectionSimilarity
    */
  def sectionsToSimilarity(sectionA: FullSection, sectionB: FullSection): SectionSimilarity = {
    var sectionSimilarity: SectionSimilarity = null

    // check if you should even process the comp beforehand (length differences,same section, etc.)
    if (isSimilarLength(sectionA,sectionB,.8) && sectionA!=sectionB ) {

      var pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim = 0.0

      var maxSimTotal = 0.0 // save section with highest total? use as baseline? nah
      var currSimTotal = 0.0

      pitchRawSim = Helper.cosineSimilarity(sectionA.getPitchRawArr, sectionB.getPitchRawArr)
      timbreRawSim = Helper.cosineSimilarity(sectionA.getTimbreRawArr, sectionB.getTimbreRawArr)
      pitchCountSim = Helper.cosineSimilarity(sectionA.getPitchCountArr, sectionB.getPitchCountArr)
      timbreCountSim = Helper.cosineSimilarity(sectionA.getTimbreCountArr, sectionB.getTimbreCountArr)
      loudnessMaxSim = Helper.cosineSimilarity(sectionA.getLoudnessMaxArr, sectionB.getLoudnessMaxArr)
      loudnessMaxTimeSim = Helper.cosineSimilarity(sectionA.getLoudnessMaxTimeArr, sectionB.getLoudnessMaxTimeArr)
      loudnessStartSim = Helper.cosineSimilarity(sectionA.getLoudnessStartArr, sectionB.getLoudnessStartArr)

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

  /**
    * Find which segment starts a section
    *
    * @param sectionsStart Array[Double] of section start times
    * @param segmentsStart Array[Double] of segment start times
    * @return Array[Int](sectionsStart.length) of segment indices that starts each section
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

  /**
    * Check if the length of secA and secB are within a certain threshold of each other
    *
    * @param sectionA FullSection
    * @param sectionB FullSection
    * @param threshold how far is similar?
    * @return true if they are within the threshold
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

  /**
    * Defines what determines a successful SectionSimilarity
    *
    * @param secSim SectionSimilarity
    * @param maxTot highest combination of all sim values
    * @return true if approved match
    */
  def isMatch(secSim: SectionSimilarity, maxTot: Double): Boolean = {
    //    val maxDistance = 99.0 / 100.0
    //    secSim.getTotalSim >= maxTot * maxDistance &&
    //        secSim.getSecA.getConfidence < .5 && secSim.getSecB.getConfidence < .5 &&
    (secSim.getPitchRawSim + secSim.getTimbreRawSim) > 1 &&
      secSim.getPitchCountSim > .99 &&
      secSim.getTimbreCountSim > .99 &&
      secSim.getLoudnessMaxSim + secSim.getLoudnessMaxTimeSim + secSim.getLoudnessStartSim > 2.42 &&
      (secSim.getSecA.getEndTime != secSim.getSecB.getStartTime) &&
      (secSim.getSecA.getStartTime != secSim.getSecB.getEndTime)
  }

}
