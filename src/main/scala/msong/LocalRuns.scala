package msong

import java.io.{File, FileInputStream, PrintWriter}

import msong.hdf5Parser.MSongHDF5Parser
import msong.section.SectionSimilarity
import org.apache.commons.io.IOUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer

/**
  * Created by jcdvorchak on 7/7/2016.
  */
object LocalRuns {

  def main(args: Array[String]): Unit = {

    val inputPath = args(0)//= "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data"
    val outputPath = args(1)//= "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\10k_output.txt"

    trackAnalysisCsv(inputPath, outputPath)
  }

  def trackAnalysisCsv(inputPath: String, outputPath: String) {
    val outputFile = new File(outputPath)
    if (outputFile.exists()) {
      error("output file exists")
    } else {
      outputFile.createNewFile()
    }
    val pw = new PrintWriter(outputFile)

    val dir = new File(inputPath)
    val dirList = getRecursiveListOfFiles(dir)
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
              pw.write(secSim.toCsv)
            }
            pw.flush()
          }
        }
      }

      //      secSimList.foreach(sim => println(sim.toString()))
      //      secSimList.foreach(sim => println(sim.toCsv))
      processedCount+=1
      if (processedCount%10000 == 0) {
        println("Checkpoint -- file " + processedCount)
      }
    }
    pw.close()
  }

  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val children = dir.listFiles

    val files = children.filter(_.isFile)
    val directories = children.filter(_.isDirectory)

    // return files and whatever you can recursively grab from any sub directories
    files ++ directories.flatMap(getRecursiveListOfFiles)
  }

}
