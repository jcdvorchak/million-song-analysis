package analysis

import java.io.{File, FileInputStream}
import java.util

import msong.hdf5Parser.MSongHDF5Parser
import msong.section.SectionSimilarity
import msong.{TrackSectionAnalysis, Analysis}
import msongdb.hdf5_getters
import org.junit.{Before, Test}
import ncsa.hdf.`object`.h5.H5File
import org.apache.commons.io.IOUtils

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * Created by jcdvorchak on 7/3/2016.
  */
@Test
class TrackSectionAnalysisTest {
  val fileContent = IOUtils.toByteArray(this.getClass.getResourceAsStream("/TRAAAAW128F429D538.h5"))
  val fullTrack = MSongHDF5Parser.readHDF5File(fileContent)

  val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A"
  //  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A\\TRAAABD128F429CF47.h5"
  //  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\B\\R\\TRABRMJ128E0780E42.h5"
  // a new

  @Before
  def setup() = {

  }

  @Test
  def directory() = {
    val dir = new File(inputPath)
    val dirList = getRecursiveListOfFiles(dir)
    println("Found " + dirList.length + " files.")
    dirList.foreach { file =>

      val currTrack = MSongHDF5Parser.readHDF5File(IOUtils.toByteArray(new FileInputStream(file)))

      val sectionArr = TrackSectionAnalysis.trackToSections(currTrack)

      //    val secSimArr = sectionArr.reduce((a,b) => TrackSectionAnalysis.sectionsToSimilarity(a,b))
      val secSimList = new ListBuffer[SectionSimilarity]()
      var secSim: SectionSimilarity = null
      for (i <- sectionArr.indices) {
        for (j <- sectionArr.indices) {
          secSim = TrackSectionAnalysis.sectionsToSimilarity(sectionArr(i), sectionArr(j))
          if (secSim != null) {
            secSimList.add(secSim)
          }
        }
      }

//      secSimList.foreach(sim => println(sim.toString()))
      secSimList.foreach(sim => println(sim.toCsv))
    }
  }

  @Test
  def trackToSectionTest(): Unit = {
    val sectionArr = TrackSectionAnalysis.trackToSections(fullTrack)

    sectionArr.foreach(sec => println(sec.toString))
  }

  @Test
  def sectionsToSimilarityTest(): Unit = {
    val sectionArr = TrackSectionAnalysis.trackToSections(fullTrack)

    //    val secSimArr = sectionArr.reduce((a,b) => TrackSectionAnalysis.sectionsToSimilarity(a,b))
    val secSimList = new ListBuffer[SectionSimilarity]()
    var secSim: SectionSimilarity = null
    for (i <- 0 until sectionArr.length) {
      for (j <- i until sectionArr.length) {
        secSim = TrackSectionAnalysis.sectionsToSimilarity(sectionArr(i), sectionArr(j))
        if (secSim != null) {
          secSimList.add(secSim)
        }
      }
    }

//    secSimList.foreach(sim => println(sim.toString()))
//    secSimList.foreach(sim => println(sim.toCsv))
  }

  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val children = dir.listFiles

    val files = children.filter(_.isFile)
    val directories = children.filter(_.isDirectory)

    // return files and whatever you can recursively grab from any sub directories
    files ++ directories.flatMap(getRecursiveListOfFiles)
  }

}
