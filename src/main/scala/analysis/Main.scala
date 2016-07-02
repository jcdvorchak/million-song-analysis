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

    analysis.attrByGenreDecade(dirList)
//    analysis.keyTrendByGenreYear(dirList)

//        val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A\\TRAAAAW128F429D538.h5"
//        val args = new Array[String](1)
//        args(0) = filePath
//        hdf5_getters.main(args)
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
