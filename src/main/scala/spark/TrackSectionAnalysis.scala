package spark

import msong.hdf5Parser.MSongHDF5Parser
import msong.{TrackSectionAnalysis, TrackSectionAnalysisLocal}
import msong.section.{SectionSimilarity, FullSection, Section}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * Created by jcdvorchak on 7/5/2016.
  */
object TrackSectionAnalysis {

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println("Usage: spark.TrackSectionAnalysis <inputPath> <outputPath>")
      System.exit(1)
    }
    val inputPath = args(0)
    val outputPath = args(1)

    val sparkConf = new SparkConf().setAppName("FullTrack Section Analysis")
    val sc = new SparkContext(sparkConf)

    val inputData = sc.binaryFiles(inputPath)

    val tracks = inputData.map { pair =>
      MSongHDF5Parser.readHDF5File(pair._1, pair._2.toArray())
    }

    //    val artists = tracks.map(track => track.getArtistName)
    //    artists.saveAsTextFile(outputPath)
    //    val similarities = tracks.map(track => new TrackSectionAnalysisLocal(track).findSimilarSections())
    //    similarities.saveAsTextFile(outputPath)

    val sections: RDD[Tuple2[Tuple2[String, String], FullSection]] = tracks.flatMap { track =>
      val sectionArr = new ListBuffer[Tuple2[Tuple2[String, String], FullSection]]

      msong.TrackSectionAnalysis.trackToSections(track).foreach { sec =>
        if (sec!=null) {
          sectionArr.add(((track.getArtistName, track.getTrackName), sec))
        }
      }

      sectionArr
    }

    val similarities = sections.groupByKey().flatMap { pair =>
      val key = pair._1
      val sectionArr = pair._2.toArray
      val secSimList = new ListBuffer[SectionSimilarity]()
      var secSim: SectionSimilarity = null
//      pair._2.foreach{i =>
//        pair._2.foreach { j =>
      for (i <- 0 until pair._2.iterator.length) {
        for (j <- i until pair._2.iterator.length) {
          secSim = msong.TrackSectionAnalysis.sectionsToSimilarity(sectionArr(i),sectionArr(j))
          if (secSim!=null) {
            secSimList.add(secSim)
          }
        }
      }

      secSimList
    }

    //secSimList needs to be formatted bro

    val matches = similarities.filter(sim => sim != null)

    matches.saveAsTextFile(outputPath)

    // make tracksectionanalysis static broh
    // make section hold more (have a thin section and a thick section) -- basically not the huge arrays
    // make a thin track object too for later -- basically not the huge arrays
    // FLATMAP A TRACK TO KEYVAL(ARTIST/TRACK),SECTION
    // need a func to go from track to sectinos
    // REDUCE BY KEY WHERE A,B ARE SECTIONS OF A TRACK, KEYVAL WOULD BE ARTIST TRACK
    // need a func to compare two sections
    // can skip func if they are way dif lengths, or next to each other, w/e

    // SHOULD END UP WITH AN OBJ LIKE KEY--ARTIST/TRACK--VAL LIST OF TRACK SecSim objects

    //    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    //    import sqlContext.implicits._

  }


}
