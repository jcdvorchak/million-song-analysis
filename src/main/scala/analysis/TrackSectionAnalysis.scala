package analysis

import java.util

import scala.collection.JavaConversions._

import msongdb.hdf5_getters
import ncsa.hdf.`object`.h5.H5File

/**
  * Created by jcdvorchak on 7/3/2016.
  */
class TrackSectionAnalysis(h5File: H5File) {
  object Break extends Exception {}

  // used for breaking out loops

  private val artist = hdf5_getters.get_artist_name(h5File)
  private val track = hdf5_getters.get_title(h5File)

  private val sectionsStart = hdf5_getters.get_sections_start(h5File)
  private val sectionConf = hdf5_getters.get_sections_confidence(h5File)
  private val segmentsStart = hdf5_getters.get_segments_start(h5File)
  private val segmentsConf = hdf5_getters.get_segments_confidence(h5File)

  private val sectionCount = sectionsStart.length
  private val segmentCount = segmentsStart.length

  private val pitches = hdf5_getters.get_segments_pitches(h5File)
  private val timbres = hdf5_getters.get_segments_timbre(h5File)
  private val loudnessMax = hdf5_getters.get_segments_loudness_max(h5File)
  private val loudnessMaxTime = hdf5_getters.get_segments_loudness_max_time(h5File)
  private val loudnessStart = hdf5_getters.get_segments_loudness_start(h5File)

  private val sectionPitchRaw, sectionTimbreRaw, sectionLoudnessMax, sectionLoudnessMaxTime, sectionLoudnessStart = new Array[Array[Double]](sectionsStart.length)
  private val sectionPitchCount, sectionTimbreCount = new Array[Array[Int]](sectionCount)
  private var maxSimTotal = 0.0

  def findSimilarSections(): String = {
    flattenTrackAudioData()
    val simMatrix = generateSimilarities()
//    simMatrix.foreach(x=>x.foreach(println))
    val result = findRelativelySimilar(simMatrix, maxSimTotal)

    result
  }

  def flattenTrackAudioData() {
    // array with each sections corresponding first segment
//    val sectionPitchRaw, sectionTimbreRaw, sectionLoudnessMax, sectionLoudnessMaxTime, sectionLoudnessStart = new Array[Array[Double]](sectionsStart.length)
    val sectionSegmentStartIndex = getSectionSegmentStartIndex(sectionsStart, segmentsStart)

    var segStartIndex, segEndIndex: Int = -1

    for (i <- 0 until sectionCount - 1) {
      // for each section
      segStartIndex = sectionSegmentStartIndex(i)
      if (i < sectionSegmentStartIndex.length) {
        segEndIndex = sectionSegmentStartIndex(i + 1)
      } else {
        segEndIndex = segmentCount - 1
      }

      val pitchCountMap, timbreCountMap = new util.TreeMap[Double, Int]()
      val pitchRawArr, timbreRawArr = new Array[Double]((segEndIndex - segStartIndex) * 12)
      val loudnessMaxArr, loudnessMaxTimeArr, loudnessStartArr = new Array[Double](segEndIndex - segStartIndex)
      var segInnerIndex, vectorIndex: Int = -1
      var count = 0
      var pitchRound, timbreRound = 0.0

      // loop from our start to the index before the next start
      for (j <- segStartIndex until segEndIndex) {
        // for each segment in this section
        loudnessMaxArr(count) = loudnessMax(j)
        loudnessMaxTimeArr(count) = loudnessMaxTime(j)
        loudnessStartArr(count) = loudnessStart(j)

        segInnerIndex = j * 12
        vectorIndex = count * 12
        for (l <- 0 until 12) {
          // for each pitch/timbre in this segment
          try {
            // wrapped in a try so I can break by throwning a Done exception
            if (segInnerIndex + l >= segmentCount * 12) {
              throw Break
            }

            pitchRawArr(vectorIndex + l) = pitches(segInnerIndex + l)
            timbreRawArr(vectorIndex + 1) = timbres(segInnerIndex + 1)

            pitchRound = pitches(segInnerIndex + l).toString.charAt(2).asDigit.toDouble / 10
            timbreRound = timbres(segInnerIndex + l).toString.charAt(2).asDigit.toDouble / 10
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

      sectionPitchRaw(i) = pitchRawArr
      sectionTimbreRaw(i) = timbreRawArr
      sectionPitchCount(i) = pitchCountMap.valuesIterator.toArray
      sectionTimbreCount(i) = timbreCountMap.valuesIterator.toArray
      sectionLoudnessMax(i) = loudnessMaxArr
      sectionLoudnessMaxTime(i) = loudnessMaxTimeArr
      sectionLoudnessStart(i) = loudnessStartArr
    }
  }

  def generateSimilarities(): Array[Array[SectionSimilarity]] = {
    var pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim = 0.0

    maxSimTotal = 0.0
    var currSimTotal = 0.0

    var currSectionA: Section = null
    var currSectionB: Section = null
    val secSimMatrix = Array.ofDim[SectionSimilarity](sectionCount, sectionCount)

    for (i <- 0 until sectionCount) {
      for (j <- i + 1 until sectionCount - 1) {
        //        for (j <- sectionPitchRaw.indices) {
        pitchRawSim = cosineSimilarity(sectionPitchRaw(i), sectionPitchRaw(j))
        timbreRawSim = cosineSimilarity(sectionTimbreRaw(i), sectionTimbreRaw(j))
        pitchCountSim = cosineSimilarity(sectionPitchCount(i), sectionPitchCount(j))
        timbreCountSim = cosineSimilarity(sectionTimbreCount(i), sectionTimbreCount(j))
        loudnessMaxSim = cosineSimilarity(sectionLoudnessMax(i), sectionLoudnessMax(j))
        loudnessMaxTimeSim = cosineSimilarity(sectionLoudnessMaxTime(i), sectionLoudnessMaxTime(j))
        loudnessStartSim = cosineSimilarity(sectionLoudnessStart(i), sectionLoudnessStart(j))

        currSimTotal = pitchRawSim + timbreRawSim + pitchCountSim + timbreCountSim + loudnessMaxSim + loudnessMaxTimeSim + loudnessStartSim
        if (currSimTotal > maxSimTotal) {
          maxSimTotal = currSimTotal
        }

        currSectionA = new Section(artist, track, sectionsStart(i), sectionsStart(i + 1), sectionConf(i))
        currSectionB = new Section(artist, track, sectionsStart(j), sectionsStart(j + 1), sectionConf(j))
        secSimMatrix(i)(j) = new SectionSimilarity(currSectionA, currSectionB, pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim)
      }
    }

    secSimMatrix
  }

  def findRelativelySimilar(matrix: Array[Array[SectionSimilarity]], max: Double): String = {
    val strBuilder = new StringBuilder
    val maxDistance = 99.5 / 100.0

    strBuilder.append(matrix(0)(1).getArtist)
      .append(" - ")
      .append(matrix(0)(1).getTrack)
      .append("\n")

    // can skip some of these, there may be duplicates and if i=j there is no val
    var currSim: SectionSimilarity = null
    for (i <- matrix.indices) {
      for (j <- matrix.indices) {
        currSim = matrix(i)(j)
        if (currSim != null) {
          if (currSim.getTotalSim >= max * maxDistance) {
            if (currSim.isSimilarLength(0.9)) {
              //} && currSim.isSectionConfident(0.5)) {
              strBuilder
                .append(currSim.getTimeRangeStr)
                .append("\ttotal: ")
                .append(matrix(i)(j).getTotalSim)
                .append("\n")
            }
          }
        }
      }
    }
    //if (pitchRawSim > 0.75 && timbreRawSim > 0.75 && pitchCountSim > 0.98 && timbreCountSim > 0.98) {
    //if (pitchRawSim + timbreRawSim > 1.5 && pitchCountSim + timbreCountSim > 1.96) {
    //if (loudnessMaxSim>0.95&&loudnessMaxTimeSim>0.5 && loudnessStartSim>0.97) {
    //if (loudnessMaxSim + loudnessMaxTimeSim + loudnessStartSim > 2.42) {

    strBuilder.toString
  }

  /*
 * Find which segment starts a section
 * First one will skip ahead with this algo, its always 0 tho a*b time complex
 */
  def getSectionSegmentStartIndex(sectionsStart: Array[Double], segmentsStart: Array[Double]): Array[Int] = {
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
