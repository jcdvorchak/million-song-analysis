package spark

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._
import msongdb.hdf5_getters
import ncsa.hdf.`object`.h5.H5File
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.spark.io.SnappyCompressionCodec

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

//    System.setProperty("java.library.path","/home/hadoop/lib")

    val sparkConf = new SparkConf().setAppName("Track Section Analysis")
    sparkConf.set("spark.hadoop.fs.s3n.impl","org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    sparkConf.set("spark.hadoop.fs.s3.impl","org.apache.hadoop.fs.s3.S3FileSystem");
    val sc = new SparkContext(sparkConf)

    //    val binaryInput = sc.binaryFiles(inputPath)

    //    val h5Files = binaryInput.map { pair =>
    //      val name = pair._1
    //      val data = pair._2.toArray()
    //
    //    }

    val filePaths = sc.textFile(inputPath) //,minPartitions=5)
//    filePaths.saveAsTextFile(outputPath)

    val h5Files = filePaths.map ( line => hdf5_getters.hdf5_open_readonly(line) )

    val artists = h5Files.map(x => (hdf5_getters.get_artist_name(x),""))
//    val artists = h5Files.map(x => x.getName)

    artists.saveAsTextFile(outputPath)

//    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
//    import sqlContext.implicits._

  }


}
