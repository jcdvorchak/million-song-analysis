package analysis

import java.io.File

import msongdb.hdf5_getters


/**
  * Created by jcdvorchak on 6/30/2016.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val analysis = new Analysis
    val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data"
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
return
    dirList.foreach { file =>
      var name = hdf5_getters.get_artist_name(hdf5_getters.hdf5_open_readonly(file.getAbsolutePath))
      if (name.contains("blink")||name.contains("Blink")||name.contains("ellowcard")) {
        println(name)
        println(file.getAbsolutePath)
      }
    }
    return


    var good = 0
    var bad = 0
    dirList.foreach { file =>
      val h5File = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)

      try {
        println(hdf5_getters.get_sections_start(h5File).length) // ~10
        println(hdf5_getters.get_segments_start(h5File).length) // ~500-1000
        println(hdf5_getters.get_segments_start(h5File)(0))
        println(hdf5_getters.get_segments_loudness_max(h5File).length) // ~500-1000
        println(hdf5_getters.get_segments_loudness_max(h5File)(0))
        println(hdf5_getters.get_segments_pitches(h5File).length) // 12 for each segment
        println(hdf5_getters.get_segments_pitches(h5File)(0))
        println(hdf5_getters.get_segments_timbre(h5File).length) // 12 for each segment
        println(hdf5_getters.get_segments_timbre(h5File)(0))
        good = good+1
      } catch {
        case e: Exception => bad = bad+1
      }
      println
      return
    }

    println(good)
    println(bad)


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
