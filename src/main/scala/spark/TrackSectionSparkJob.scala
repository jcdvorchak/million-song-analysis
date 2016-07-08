package spark

import msong.hdf5Parser.MSongHDF5Parser
import msong.section.{SectionSimilarity, FullSection}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * Spark Job to process Track Sections and find their similarities.
  * Relies on TrackSetionAnalysis heavily
  *
  * Created by jcdvorchak on 7/5/2016.
  */
object TrackSectionSparkJob {

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println("Usage: spark.TrackSectionSparkJob <inputPath> <outputPath>")
      System.exit(1)
    }
    val inputPath = args(0)
    val outputPath = args(1)

    val sparkConf = new SparkConf().setAppName("Track Section Analysis")
    val sc = new SparkContext(sparkConf)

    val inputData = sc.binaryFiles(inputPath)

    val tracks = inputData.map { pair =>
      MSongHDF5Parser.readHDF5File(pair._1, pair._2.toArray())
    }

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
      val secSimList = new ListBuffer[String]()
      var secSim: SectionSimilarity = null

      for (i <- 0 until pair._2.iterator.length) {
        for (j <- i until pair._2.iterator.length) {
          secSim = msong.TrackSectionAnalysis.sectionsToSimilarity(sectionArr(i),sectionArr(j))
          if (secSim!=null) {
            secSimList.add(secSim.toCsv)
          }
        }
      }

      secSimList
    }

    val matches = similarities.filter(line => line != null || line == "")

    matches.saveAsTextFile(outputPath)
  }
}
