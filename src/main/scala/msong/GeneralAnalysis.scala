package msong

import java.io.{File, FileInputStream}

import msong.hdf5Parser.MSongHDF5Parser
import msong.track.FullTrack

import scala.collection.JavaConversions._
import org.apache.commons.io.IOUtils

import scala.collection.mutable.ListBuffer

/**
  * Some simpler data exploration
  * Aggregation of TrackSectionAnalysis output
  *
  * Created by jcdvorchak on 7/2/2016.
  */
object GeneralAnalysis {
  val outputPath = "C:\\Users\\Admin\\Documents\\GitHub\\million-song-analysis\\output\\output.txt"

  def keyTrendByGenreYear(dirList: Array[File]): ListBuffer[String] = {
    var track: FullTrack = null

    var strBuilder: StringBuilder = null
    val lineList = new ListBuffer[String]()
    var termArr: Array[String] = null

    dirList.foreach { currFile =>

      track = MSongHDF5Parser.readHDF5File(IOUtils.toByteArray(new FileInputStream(currFile)))

      strBuilder = new StringBuilder
      strBuilder.append(track.getArtistName).append(",")
        .append(track.getTrackName).append(",")
        .append(track.getTop3GenreCsv)
        .append(track.getKey).append(",")
        .append(track.getKeyConfidence).append(",")
        .append(Helper.getDecade(track.getYear.toInt))

      val grouped = lineList.groupBy { line =>
        val lineArr = line.split(",")
        lineArr(3) + "," + lineArr(2) + "," + lineArr(5)
      }

      println("groupBy " + grouped.size)

      val output = grouped.map { pair =>
        pair._1 + "," + pair._2.length
      }

      //      Helper.writeToFile(outputPath, output)
    }

    lineList
  }

  def attrByGenreDecade(dirList: Array[File]): ListBuffer[String] = {
    var track: FullTrack = null

    var strBuilder: StringBuilder = null
    val lineList = new ListBuffer[String]()
    var termArr: Array[String] = null

    dirList.foreach { currFile =>
      track = MSongHDF5Parser.readHDF5File(IOUtils.toByteArray(new FileInputStream(currFile)))

      termArr = track.getTop3Genre

      strBuilder = new StringBuilder
      termArr.foreach { term =>
        if (term != "") {
          strBuilder.append(term).append(",")
            .append(track.getYear).append(",")
            .append(track.getDanceability).append(",")
            .append(track.getLoudness).append(",")
            .append(track.getTempo).append(",")
            .append(track.getEnergy)

          lineList.add(strBuilder.toString())
        }
      }
    }

    Helper.writeToFile(outputPath, lineList)

    val grouped = lineList.groupBy { line =>
      val lineArr = line.split(",")
      lineArr(0) + "," + lineArr(1) + "," + lineArr(2)
    }

    println("groupBy " + grouped.size)

    lineList

    //    val output = grouped.map { pair =>
    //      val key = pair._1
    //      val lineList = pair._2
    //    }

    //    Helper.writeToFile(outputPath, output)
  }

  /**
    * Find the weighted mean of danceability,loudness,tempo,energy by genre and year
    * Weight using the genre weight of that track
    * Expects lines in the format genre,weight,year,danceability,loudness,tempo,energy
    *
    * @param lineList Array[String] of output lines
    */
  def trackDataWeightedMean(lineList: ListBuffer[String]): Array[String] = {

    var genreDecade = lineList.map { line =>
      val lineArr = line.split(",")
      ((lineArr(0), lineArr(2)), (lineArr(1), lineArr(3), lineArr(4), lineArr(5), lineArr(6)))
    }

    genreDecade = genreDecade.filter(pair => !pair._1._2.toInt.equals(-1))

    val agg = genreDecade.groupBy(keyval => keyval._1).map { keyval =>
      val key = keyval._1
      val valueList = keyval._2
      println(key)
      println(valueList)

      var dTot, lTot, tTot, eTot: Double = 0.0
      var div: Double = 0.0
      var weight: Double = 0.0

      valueList.foreach { keyval =>
        val record = keyval._2
        weight = record._1.toDouble
        div = div + weight

        dTot = dTot + (record._2.toDouble * weight)
        lTot = lTot + (record._3.toDouble * weight)
        tTot = tTot + (record._4.toDouble * weight)
        eTot = eTot + (record._5.toDouble * weight)
      }

      key._1 + "," + key._2 + "," + (dTot / div) + "," + (lTot / div) + "," + (tTot / div) + "," + (eTot / div)
    }

    agg.toArray[String]
  }

  /**
    * Aggregate track section similarities by genre
    *
    * Expects output of LocalRuns.trackAnalysisPsv
    * artistName|trackName|year|artistLocation|songHotttnesss|genre1|genre2|genre3|
    * sectionATime|sectionALength|sectionBTime|sectionBLength|pitchSim|timbreSim|
    * pitchCountSim|timbreCountSim|loudnessMaxSim|loudnessMaxTimeSim|loudnessStartSim
    */
  def sectionSimilarityCountGenre(lineList: List[String]): Array[String] = {
    // genre,simTotal,avgSimPerTrack

    var result: ListBuffer[Tuple2[String, String]] = null
    var duration, hotttnesss: Double = 0.0
    var trackName: String = ""
    var lineArr: Array[String] = null
    var len = 0
    val keyval = lineList.flatMap { line =>
      result = new ListBuffer[Tuple2[String, String]]
      lineArr = line.split("\\|")
      len = lineArr.length
      try {
//        duration = (lineArr(9).toDouble + lineArr(11).toDouble) / 2.0
        duration = (lineArr(len-10).toDouble + lineArr(len-8).toDouble) / 2.0
      } catch {
        case e: NumberFormatException => {
          duration = 0.0
          println("duration NFE")
        }
      }
      try {
//        hotttnesss = lineArr(4).toDouble
        hotttnesss = lineArr(len-15).toDouble
      } catch {
        case e: NumberFormatException => {
          hotttnesss = 0.0
          println("hotttnesss NFE")
        }
      }
      trackName = lineArr(1)

      for (i <- 0 until 3) {
        if (lineArr((len-14) + i) != "") {
          // genre,trackName,avgDurationOfSecSim,hotttnesss
          result.add((lineArr((len-14) + i), trackName + "|" + duration + "|" + hotttnesss))
        }
      }

      result
    }

    val output = keyval.groupBy(pair => pair._1).map { group =>
      var durationTotal, hotttnesssTotal: Double = 0.0
      var simCount: Int = 0
      var lineArr: Array[String] = null
      group._2.foreach { line =>
        lineArr = line._2.split("\\|")
        simCount += 1
        durationTotal += lineArr(1).toDouble
        hotttnesssTotal += lineArr(2).toDouble
      }

      group._1.toString + "|" + simCount + "|" + (durationTotal / simCount) + "|" + (hotttnesssTotal / simCount)
    }

    output.toArray[String]
  }

  /**
    * Aggregate track section similarities by decade
    *
    * Expects output of LocalRuns.trackAnalysisCsv
    * artistName|trackName| year|artistLocation|songHotttnesss|genre1|genre2|genre3|
    * sectionATime|sectionALength|sectionBTime|sectionBLength|pitchSim|timbreSim|
    * pitchCountSim|timbreCountSim|loudnessMaxSim|loudnessMaxTimeSim|loudnessStartSim
    */
  def sectionSimilarityCountDecade(lineList: List[String]): Array[String] = {
    // decade,simTotal,avgSimPerTrack

    var duration, hotttnesss: Double = 0.0
    var trackName: String = ""
    var lineArr: Array[String] = null
    var len,year = 0
    val keyval = lineList.map { line =>
      lineArr = line.split("\\|")
      len = lineArr.length
      try {
        //        duration = (lineArr(9).toDouble + lineArr(11).toDouble) / 2.0
        duration = (lineArr(len-10).toDouble + lineArr(len-8).toDouble) / 2.0
      } catch {
        case e: NumberFormatException => {
          duration = 0.0
          println("duration NFE")
        }
      }
      try {
        //        hotttnesss = lineArr(4).toDouble
        hotttnesss = lineArr(len-15).toDouble
      } catch {
        case e: NumberFormatException => {
          hotttnesss = 0.0
          println("hotttnesss NFE")
        }
      }
      trackName = lineArr(1)

      // decade,trackName,avgDurationOfSecSim,hotttnesss
      try {
        year = lineArr(2).toInt
      } catch {
        case e: NumberFormatException => {
          year = 0
//          println("year NFE")
        }
      }
      (Helper.getDecade(year), trackName + "|" + duration + "|" + hotttnesss)
    }.filter(pair => pair._1 != -1)

    val output = keyval.groupBy(pair => pair._1).map { group =>
      var durationTotal, hotttnesssTotal: Double = 0.0
      var simCount: Int = 0
      var lineArr: Array[String] = null
      group._2.foreach { line =>
        lineArr = line._2.split("\\|")
        simCount += 1
        durationTotal += lineArr(1).toDouble
        hotttnesssTotal += lineArr(2).toDouble
      }

      group._1.toString + "|" + simCount + "|" + (durationTotal / simCount) + "|" + (hotttnesssTotal / simCount)
    }

    output.toArray[String]
  }

  /**
    * Aggregate track section similarities by location
    *
    * Expects output of LocalRuns.trackAnalysisCsv
    * artistName|trackName| year|artistLocation|songHotttnesss|genre1|genre2|genre3|
    * sectionATime|sectionALength|sectionBTime|sectionBLength|pitchSim|timbreSim|
    * pitchCountSim|timbreCountSim|loudnessMaxSim|loudnessMaxTimeSim|loudnessStartSim
    */
  def sectionSimilarityCountLocation(lineList: List[String]): Array[String] = {
    // location,simTotal,avgSimPerTrack

    var duration, hotttnesss: Double = 0.0
    var trackName: String = ""
    var lineArr: Array[String] = null
    var len = 0
    val keyval = lineList.map { line =>
      lineArr = line.split("\\|")
      len = lineArr.length
      try {
        //        duration = (lineArr(9).toDouble + lineArr(11).toDouble) / 2.0
        duration = (lineArr(len-10).toDouble + lineArr(len-8).toDouble) / 2.0
      } catch {
        case e: NumberFormatException => {
          duration = 0.0
          println("duration NFE")
        }
      }
      try {
        //        hotttnesss = lineArr(4).toDouble
        hotttnesss = lineArr(len-15).toDouble
      } catch {
        case e: NumberFormatException => {
          hotttnesss = 0.0
          println("hotttnesss NFE")
        }
      }
      trackName = lineArr(1)

      // Location,trackName,avgDurationOfSecSim,hotttnesss
      (lineArr(len-16), trackName + "|" + duration + "|" + hotttnesss)
    }.filter(pair => pair._1 != "")

    val output = keyval.groupBy(pair => pair._1).map { group =>
      var durationTotal, hotttnesssTotal: Double = 0.0
      var simCount: Int = 0
      var lineArr: Array[String] = null
      group._2.foreach { line =>
        lineArr = line._2.split("\\|")
        simCount += 1
        durationTotal += lineArr(1).toDouble
        hotttnesssTotal += lineArr(2).toDouble
      }

      group._1.toString + "|" + simCount + "|" + (durationTotal / simCount) + "|" + (hotttnesssTotal / simCount)
    }

    output.toArray[String]
  }

  /**
    * Aggregate track section similarities by avg hotttness and duration
    *
    * Expects output of LocalRuns.trackAnalysisCsv
    * artistName|trackName| year|artistLocation|songHotttnesss|genre1|genre2|genre3|
    * sectionATime|sectionALength|sectionBTime|sectionBLength|pitchSim|timbreSim|
    * pitchCountSim|timbreCountSim|loudnessMaxSim|loudnessMaxTimeSim|loudnessStartSim
    */
  def sectionSimilarityCountHotttnesss(lineList: List[String]): Array[String] = {
    // duration rounded to 1s,simTotal,avgHotttnesss

    var duration: Int = 0
      var hotttnesss: Double = 0.0
    var trackName: String = ""
    var lineArr: Array[String] = null
    var len = 0
    val keyval = lineList.map { line =>
      lineArr = line.split("\\|")
      len = lineArr.length
      try {
        //        duration = (lineArr(9).toDouble + lineArr(11).toDouble) / 2.0
        duration = ((lineArr(len-10).toDouble + lineArr(len-8).toDouble)/2.0).toInt
      } catch {
        case e: NumberFormatException => {
          duration = 0
          println("duration NFE")
        }
      }
      try {
        //        hotttnesss = lineArr(4).toDouble
        hotttnesss = lineArr(len-15).toDouble
      } catch {
        case e: NumberFormatException => {
          hotttnesss = 0.0
          println("hotttnesss NFE")
        }
      }
      trackName = lineArr(1)

      // Location,trackName,avgDurationOfSecSim,hotttnesss
      (duration, trackName + "|" + hotttnesss)
    }.filter(pair => pair._1 != "")

    val output = keyval.groupBy(pair => pair._1).map { group =>
      var hotttnesssTotal: Double = 0.0
      var simCount: Int = 0
      var lineArr: Array[String] = null
      group._2.foreach { line =>
        lineArr = line._2.split("\\|")
        simCount += 1
        hotttnesssTotal += lineArr(1).toDouble
      }

      group._1.toString + "|" + simCount + "|" + (hotttnesssTotal / simCount)
    }

    output.toArray[String]
  }
}
