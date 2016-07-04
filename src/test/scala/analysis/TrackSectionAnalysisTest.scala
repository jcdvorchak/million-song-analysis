package analysis

import java.io.File

import msongdb.hdf5_getters
import org.junit.{Before, Test}
import ncsa.hdf.`object`.h5.H5File

/**
  * Created by jcdvorchak on 7/3/2016.
  */
@Test
class TrackSectionAnalysisTest {
  object Break extends Exception {} // used for breaking out loops

  val analysis: Analysis = new Analysis

  //  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A\\TRAAABD128F429CF47.h5"
  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\B\\R\\TRABRMJ128E0780E42.h5"
  // a new hope
  val h5File: H5File = hdf5_getters.hdf5_open_readonly(filePath)

  @Before
  def setup() = {

  }

  @Test
  def findSimilarSectionsTest(): Unit = {
    val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A"
    // gotta loop through files if there's more than one song bro

    val dir = new File(inputPath)
    val dirList = Main.getRecursiveListOfFiles(dir)
    println("Found " + dirList.length + " files.")
    dirList.foreach { file =>
      val h5File = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)
      val trackAnalysis = new TrackSectionAnalysis(h5File)
      println(trackAnalysis.findSimilarSections())
      hdf5_getters.hdf5_close(h5File)
    }

    //    analysis.findSimilarSections(hdf5_getters.hdf5_open_readonly(filePath))

    //    val h5File = hdf5_getters.hdf5_open_readonly(filePath)
    //    val trackAnalysis = new TrackSectionAnalysis(h5File)
    //    println(trackAnalysis.findSimilarSections())
    //    hdf5_getters.hdf5_close(h5File)
  }

}
