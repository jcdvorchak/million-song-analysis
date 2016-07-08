package msong

import java.io.{File, FileInputStream, PrintWriter}

import msong.hdf5Parser.MSongHDF5Parser
import msong.section.SectionSimilarity
import org.apache.commons.io.IOUtils

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Some data processing functions that run on a single machine
  *
  * Created by jcdvorchak on 7/7/2016.
  */
object LocalRuns {

  def main(args: Array[String]): Unit = {

    val inputPath = args(0) //= "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data"
    val outputPath = args(1) //= "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\10k_output.txt"

    //    trackAnalysisPsv(inputPath, outputPath)

    sectionSimilarityCounts(inputPath, outputPath)
  }

  /**
    * Run TrackSectionAnalysis on a directory of HDF5 files
    * Write the pipe separated format out to the outputPath
    *
    * @param inputPath path to parent dir of input files
    * @param outputPath path to output file
    */
  def trackAnalysisPsv(inputPath: String, outputPath: String) {
    val outputFile = new File(outputPath)
    if (outputFile.exists()) {
      error("output file exists")
    } else {
      outputFile.createNewFile()
    }
    val pw = new PrintWriter(outputFile)

    val dir = new File(inputPath)
    val dirList = Helper.getRecursiveListOfFiles(dir)
    println("Found " + dirList.length + " files.")
    var processedCount = 0
    dirList.foreach { file =>

      val currTrack = MSongHDF5Parser.readHDF5File(IOUtils.toByteArray(new FileInputStream(file)))

      val sectionArr = TrackSectionAnalysis.trackToSections(currTrack)

      if (sectionArr != null) {
        val secSimList = new ListBuffer[SectionSimilarity]()
        var secSim: SectionSimilarity = null
        for (i <- 0 until sectionArr.length) {
          for (j <- i until sectionArr.length) {
            secSim = TrackSectionAnalysis.sectionsToSimilarity(sectionArr(i), sectionArr(j))
            if (secSim != null) {
              pw.println(secSim.toCsv)
            }
            pw.flush()
          }
        }
      }

      processedCount += 1
      if (processedCount % 10000 == 0) {
        println("Checkpoint -- file " + processedCount)
      }
    }
    pw.close()
  }

  /**
    * Aggregate the output of TrackSectionAnalysis by genre,decade,location
    * Formatted in a PSV, going to use them for some fantastical tableau visualizations
    *
    * @param inputPath path to input file
    * @param outputPath path to output file
    */
  def sectionSimilarityCounts(inputPath: String, outputPath: String): Unit = {
    val outputDirectory = new File(outputPath)
    if (outputDirectory.isFile) {
      error("received output file, need a directory for multiple outputs")
    }

    val genreOutputFilePath = outputDirectory.getAbsolutePath + "/genreCounts.txt"
    val decadeOutputFilePath = outputDirectory.getAbsolutePath + "/decadeCounts.txt"
    val locationOutputFilePath = outputDirectory.getAbsolutePath + "/locationCounts.txt"

    val lineList = Source.fromFile(inputPath).getLines.toList

    Helper.writeToFile(genreOutputFilePath, GeneralAnalysis.sectionSimilarityCountGenre(lineList))
    Helper.writeToFile(decadeOutputFilePath, GeneralAnalysis.sectionSimilarityCountDecade(lineList))
    Helper.writeToFile(locationOutputFilePath, GeneralAnalysis.sectionSimilarityCountLocation(lineList))
  }

}
