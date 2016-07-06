package analysis

import java.io.File

import msongdb.hdf5_getters
import ncsa.hdf.`object`.h5.H5File


/**
  * Created by jcdvorchak on 6/30/2016.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val analysis = new Analysis
    val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A"
    // gotta loop through files if there's more than one song bro

    val dir = new File(inputPath)
    val dirList = getRecursiveListOfFiles(dir)
    println("Found " + dirList.length + " files.")

    //    analysis.attrByGenreDecade(dirList)
    //    analysis.keyTrendByGenreYear(dirList)

        val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A\\TRAAAAW128F429D538.h5"
            val args = new Array[String](1)
            args(0) = filePath
            hdf5_getters.main(args)

//    var trackSectionAnalysis: TrackSectionAnalysis = null
//    var h5File: H5File = null
//    dirList.foreach{ file =>
//      h5File = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)
//      trackSectionAnalysis = new TrackSectionAnalysis(h5File)
//
//      println(trackSectionAnalysis.findSimilarSections())
//
//      hdf5_getters.hdf5_close(h5File)
//    }



//    var good = 0
//    var bad = 0
//    dirList.foreach { file =>
//      val h5File = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)
//
//      try {
//        println(hdf5_getters.get_sections_start(h5File).length) // ~10
//        println(hdf5_getters.get_segments_start(h5File).length) // ~500-1000
//        println(hdf5_getters.get_segments_start(h5File)(0))
//        println(hdf5_getters.get_segments_loudness_max(h5File).length) // ~500-1000
//        println(hdf5_getters.get_segments_loudness_max(h5File)(0))
//        println(hdf5_getters.get_segments_pitches(h5File).length) // 12 for each segment
//        println(hdf5_getters.get_segments_pitches(h5File)(0))
//        println(hdf5_getters.get_segments_timbre(h5File).length) // 12 for each segment
//        println(hdf5_getters.get_segments_timbre(h5File)(0))
//        good = good+1
//      } catch {
//        case e: Exception => bad = bad+1
//      }
//      println
//      return
//    }
//
//    println(good)
//    println(bad)
  }

  /*
  * recursively get all files in a directory, exlude sub directories
  */
  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val children = dir.listFiles

    val files = children.filter(_.isFile)
    val directories = children.filter(_.isDirectory)

    // return files and whatever you can recursively grab from any sub directories
    files ++ directories.flatMap(getRecursiveListOfFiles)
  }

}
