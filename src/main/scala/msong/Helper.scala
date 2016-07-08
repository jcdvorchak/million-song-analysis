package msong

import java.io.{File, PrintWriter}

/**
  * Contains some random helper functions used throughout
  *
  * Created by jcdvorchak on 7/7/2016.
  */
object Helper {

  /**
    * Write a list of lines to a filepath
    *
    * @param path path to output file
    * @param lineList list of lines to write
    */
  def writeToFile(path: String, lineList: Iterable[String]): Unit = {
    val pw = new PrintWriter(new File(path))

    lineList.foreach { line =>
      pw.println(line)
      pw.flush()
    }

    pw.close()
  }

  /**
    * Recursively find files in a directory and it's sub directories
    *
    * @param dir parent directory to search
    * @return array of File objects
    */
  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val children = dir.listFiles

    val files = children.filter(_.isFile)
    val directories = children.filter(_.isDirectory)

    // return files and whatever you can recursively grab from any sub directories
    files ++ directories.flatMap(getRecursiveListOfFiles)
  }

  /**
    * Determine the decade based on the songs release year
    * return -1 if it's an incorrect year format
    *
    * @param year year to convert
    * @return decade of year || -1
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

  /**
    * Find the cosine similarity between two Array[Double]
    *
    * @param vectorA
    * @param vectorB
    * @return
    */
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

  /**
    * Find the cosine similarity between two Array[Int]
    *
    * @param vectorA
    * @param vectorB
    * @return
    */
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
