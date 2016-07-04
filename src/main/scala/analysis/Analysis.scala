package analysis

import msongdb.hdf5_getters
import java.io.{File, PrintWriter}

import scala.collection.JavaConversions._
import java.util

import ncsa.hdf.`object`.h5.H5File
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception

/**
  * Created by jcdvorchak on 7/2/2016.
  */
class Analysis {
  object Break extends Exception {} // used for breaking out loops
  val outputPath = "C:\\Users\\Admin\\Documents\\GitHub\\million-song-analysis\\output\\output.txt"

  def findSimilarSections(h5File: H5File) {
    val artist = hdf5_getters.get_artist_name(h5File)
    val track = hdf5_getters.get_title(h5File)

    val sectionsStart = hdf5_getters.get_sections_start(h5File)
    val sectionConf = hdf5_getters.get_sections_confidence(h5File)
    val segmentsStart = hdf5_getters.get_segments_start(h5File)
    val segmentsConf = hdf5_getters.get_segments_confidence(h5File)

    val pitches = hdf5_getters.get_segments_pitches(h5File)
    val timbres = hdf5_getters.get_segments_timbre(h5File)
    val loudnessMax = hdf5_getters.get_segments_loudness_max(h5File)
    val loudnessMaxTime = hdf5_getters.get_segments_loudness_max_time(h5File)
    val loudnessStart = hdf5_getters.get_segments_loudness_start(h5File)

    // array with each sections corresponding first segment
    val sectionSegmentStartIndex = getSectionSegmentStartIndex(sectionsStart, segmentsStart)

    val sectionPitchCount, sectionTimbreCount = new Array[Array[Int]](sectionsStart.length)
    val sectionPitchRaw, sectionTimbreRaw, sectionLoudnessMax, sectionLoudnessMaxTime, sectionLoudnessStart = new Array[Array[Double]](sectionsStart.length)
    var segStartIndex, segEndIndex: Int = -1

    for (i <- 0 until sectionsStart.length - 1) {
      // for each section
      segStartIndex = sectionSegmentStartIndex(i)
      if (i < sectionSegmentStartIndex.length) {
        segEndIndex = sectionSegmentStartIndex(i + 1)
      } else {
        segEndIndex = segmentsStart.length - 1
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
          try { // wrapped in a try so I can break by throwning a Done exception
            if (segInnerIndex + l >= pitches.length) {
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

    var pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim = 0.0
    val good = new util.ArrayList[String]

    val simTotalMatrix = Array.ofDim[Double](sectionPitchRaw.length, sectionPitchRaw.length)
    var maxSimTotal = 0.0
    var currSimTotal = 0.0

    var currSectionA: Section = null
    var currSectionB: Section = null
    val secSimMatrix = Array.ofDim[SectionSimilarity](sectionPitchRaw.length, sectionPitchRaw.length)

    for (i <- sectionPitchRaw.indices) {
      for (j <- i + 1 until sectionPitchRaw.length - 1) {
        //        for (j <- sectionPitchRaw.indices) {
        pitchRawSim = cosineSimilarity(sectionPitchRaw(i), sectionPitchRaw(j))
        timbreRawSim = cosineSimilarity(sectionTimbreRaw(i), sectionTimbreRaw(j))
        pitchCountSim = cosineSimilarity(sectionPitchCount(i), sectionPitchCount(j))
        timbreCountSim = cosineSimilarity(sectionTimbreCount(i), sectionTimbreCount(j))
        loudnessMaxSim = cosineSimilarity(sectionLoudnessMax(i), sectionLoudnessMax(j))
        loudnessMaxTimeSim = cosineSimilarity(sectionLoudnessMaxTime(i), sectionLoudnessMaxTime(j))
        loudnessStartSim = cosineSimilarity(sectionLoudnessStart(i), sectionLoudnessStart(j))

        currSimTotal = pitchRawSim + timbreRawSim + pitchCountSim + timbreCountSim + loudnessMaxSim + loudnessMaxTimeSim + loudnessStartSim
        simTotalMatrix(i)(j) = currSimTotal
        if (currSimTotal > maxSimTotal) {
          maxSimTotal = currSimTotal
        }

        currSectionA = new Section(artist, track, sectionsStart(i), sectionsStart(i + 1), sectionConf(i))
        currSectionB = new Section(artist, track, sectionsStart(j), sectionsStart(j + 1), sectionConf(j))
        secSimMatrix(i)(j) = new SectionSimilarity(currSectionA, currSectionB, pitchRawSim, timbreRawSim, pitchCountSim, timbreCountSim, loudnessMaxSim, loudnessMaxTimeSim, loudnessStartSim)

        //        if (pitchRawSim > 0.75 && timbreRawSim > 0.75 && pitchCountSim > 0.98 && timbreCountSim > 0.98) {
        // 1.5 1.96
        if (pitchRawSim + timbreRawSim > 1.5 && pitchCountSim + timbreCountSim > 1.96) {
          //          if (loudnessMaxSim>0.95&&loudnessMaxTimeSim>0.5 && loudnessStartSim>0.97) {
          // 2.42
          if (loudnessMaxSim + loudnessMaxTimeSim + loudnessStartSim > 2.42) {
            good.add(sectionsStart(i) + "-" + sectionsStart(i + 1) + " and " + sectionsStart(j) + "-" + sectionsStart(j + 1))

          }
        }
      }
    }

    //    println(hdf5_getters.get_artist_name(h5File))
    //    println(hdf5_getters.get_title(h5File))

    //    if (good.length!=0) {
    //      println("similar: ")
    //      good.foreach(println)
    //    } else {
    //      println("no matches")
    //    }
    //    println

    findRelativelySimilar(secSimMatrix, maxSimTotal)
    println
  }

  def findRelativelySimilar(matrix: Array[Array[SectionSimilarity]], max: Double) {
    val maxDistance = 99.5 / 100.0
    //    println(max)
    //    println(maxDistance)
    //    println(max*maxDistance)
    println(matrix(0)(1).getArtist)
    println(matrix(0)(1).getTrack)

    // can skip some of these, there may be duplicates and if i=j there is no val
    var currSim: SectionSimilarity = null
    for (i <- matrix.indices) {
      for (j <- matrix.indices) {
        currSim = matrix(i)(j)
        if (currSim != null) {
          if (currSim.getTotalSim >= max * maxDistance) {
            if (currSim.isSimilarLength(0.9)) {
              //} && currSim.isSectionConfident(0.5)) {
              println(currSim.getTimeRangeStr + "\ttotal: " + matrix(i)(j).getTotalSim)
            }
          }
        }
      }
    }
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
            //            println(seg + " " + segmentIndex + " starts section " + sec)
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

    return sectionSegmentStart
  }

  def keyTrendByGenreYear(dirList: Array[File]) {
    var h5File: H5File = null
    var stringBuilder: StringBuilder = null
    val lineList = new util.ArrayList[String]()
    dirList.foreach { currFile =>
      //      println(currFile.getAbsolutePath)

      h5File = hdf5_getters.hdf5_open_readonly(currFile.getAbsolutePath)

      stringBuilder = new StringBuilder
      stringBuilder.append(hdf5_getters.get_artist_name(h5File)).append(",")
      stringBuilder.append(hdf5_getters.get_title(h5File)).append(",")
      try {
        stringBuilder.append(hdf5_getters.get_artist_terms(h5File)(0)).append(",")
      } catch {
        case he: HDF5Exception => stringBuilder.append(",")
        case e: Exception => e.printStackTrace()
      }
      stringBuilder.append(hdf5_getters.get_key(h5File)).append(",")
      stringBuilder.append(hdf5_getters.get_key_confidence(h5File)).append(",")
      stringBuilder.append(getDecade(hdf5_getters.get_year(h5File)))
      lineList.add(stringBuilder.toString)

      //      println(lineList.length)


      //      hdf5_getters.get_artist_terms(h5File).foreach(print(_))
      //      println
      //      hdf5_getters.get_artist_terms_freq(h5File).foreach{in =>
      //        print(in)
      //        print(" ")
      //      }
      //      println
      //      hdf5_getters.get_artist_terms_weight(h5File).foreach{in =>
      //        print(in)
      //        print(" ")
      //      }
      //      hdf5_getters.get_artist_mbtags(h5File).foreach{in =>
      //        print(in)
      //        print(" ")
      //      }

      //      pw.write(hdf5_getters.get_artist_name(h5File))
      //      pw.write("," + hdf5_getters.get_title(h5File))
      //      pw.write("," + hdf5_getters.get_artist_terms(h5File)(0))
      //      pw.write("," + hdf5_getters.get_key(h5File))
      //      pw.write("," + hdf5_getters.get_key_confidence(h5File))
      //      pw.write("," + hdf5_getters.get_year(h5File))
      //      pw.println()

      hdf5_getters.hdf5_close(h5File)
    }

    //    pw.flush()
    //    pw.close

    val grouped = lineList.groupBy { line =>
      val lineArr = line.split(",")
      lineArr(3) + "," + lineArr(2) + "," + lineArr(5)
    }

    println("groupBy " + grouped.size)

    val output = grouped.map { pair =>
      pair._1 + "," + pair._2.length
    }

    writeToFile(outputPath, output)

  }

  def attrByGenreDecade(dirList: Array[File]) {
    var h5File: H5File = null

    var stringBuilder: StringBuilder = null
    val lineList = new util.ArrayList[String]()

    dirList.foreach { currFile =>
      //      println(currFile.getAbsolutePath)


      h5File = hdf5_getters.hdf5_open_readonly(currFile.getAbsolutePath)

      //genre(top5diflines),genre_weight,decade,attr1,attr2,attr3,....
      var artistTerms: Array[String] = null
      try {
        artistTerms = hdf5_getters.get_artist_terms(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => artistTerms = new Array[String](0)
      }
      var artistTermWeights: Array[Double] = null
      try {
        artistTermWeights = hdf5_getters.get_artist_terms_weight(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => artistTermWeights = new Array[Double](0)
      }
      var artistTermFreq: Array[Double] = null
      try {
        artistTermFreq = hdf5_getters.get_artist_terms_freq(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => artistTermFreq = new Array[Double](0)
      }
      var year: Int = 0
      try {
        year = hdf5_getters.get_year(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => year = 0
      }
      val decade = getDecade(year)
      var danceability: Double = 0.0
      try {
        danceability = hdf5_getters.get_danceability(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => danceability = -1
      }
      var loudness: Double = 0.0
      try {
        loudness = hdf5_getters.get_loudness(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => loudness = -1
      }
      var tempo: Double = 0.0
      try {
        tempo = hdf5_getters.get_tempo(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => tempo = -1
      }
      var energy: Double = 0.0
      try {
        energy = hdf5_getters.get_energy(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => energy = -1
      }
      //      hdf5_getters.get_key(h5File) // take into account confidence here

      if (artistTerms.length != artistTermWeights.length ||
        artistTerms.length != artistTermFreq.length ||
        artistTermFreq.length != artistTermWeights.length) {
        println("Missing or mismatched artistTerms & artistTermWeights.. skipping line")
      } else {
        // TODO top 5 genre? or the ones above a frequency threshhold and weight?
        var range = 10

        if (artistTerms.length < range) {
          range = artistTerms.length
        }
        for (i <- 0 until range) {
          if (artistTermFreq(i)>=.9) {
            stringBuilder = new StringBuilder
            stringBuilder.append(artistTerms(i)).append(",")
            stringBuilder.append(artistTermWeights(i)).append(",")
            stringBuilder.append(decade).append(",")
            stringBuilder.append(danceability).append(",")
            stringBuilder.append(loudness).append(",")
            stringBuilder.append(tempo).append(",")
            stringBuilder.append(energy)

            lineList.add(stringBuilder.toString)
          }
        }
      }
    }

    writeToFile(outputPath,lineList)

    val grouped = lineList.groupBy { line =>
      val lineArr = line.split(",")
      lineArr(0) + "," + lineArr(1) + "," + lineArr(2)
    }

    println("groupBy " + grouped.size)

//    val output = grouped.map { pair =>
//      val key = pair._1
//      val lineList = pair._2
//    }
//    writeToFile(outputPath, output)

    hdf5_getters.hdf5_close(h5File)
  }

  def writeToFile(path: String, lineList: Iterable[String]): Unit = {
    val pw = new PrintWriter(new File(path))

    lineList.foreach { line =>
      pw.println(line)
      pw.flush()
    }

    pw.close()
  }

  /*
   * Determine the decade based on the songs release year
   * return -1 if it's an incorrect year format
   */
  def getDecade(year: Int): Int = {
    val str = year.toString

    if (year == 0 || str.length != 4) {
      return -1
    }

    if (str.charAt(3) != 0) {
      year - str.charAt(3).asDigit
    } else {
      year
    }
  }

  def cosineSimilarity(vectorA: Array[Double], vectorB: Array[Double]): Double = {
    var dotProduct = 0.0
    var normA = 0.0
    var normB = 0.0

    var indices = vectorA.indices
    if (vectorA.length>vectorB.length) {
      indices = vectorB.indices
    }

    for ( i <- indices) {
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
    if (vectorA.length>vectorB.length) {
      indices = vectorB.indices
    }
    for ( i <- indices) {
      dotProduct += vectorA(i) * vectorB(i)
      normA += Math.pow(vectorA(i), 2)
      normB += Math.pow(vectorB(i), 2)
    }

    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))
  }
}
