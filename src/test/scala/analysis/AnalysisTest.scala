package analysis

import java.io.File
import java.util

import msongdb.hdf5_getters
import ncsa.hdf.`object`.h5.H5File
import org.junit.{After, Before, Test}

import scala.collection.JavaConversions._

/**
  * Created by jcdvorchak on 7/2/2016.
  */
@Test
class AnalysisTest {
  object Break extends Exception {} // used for breaking out loops

  val analysis: Analysis = new Analysis

  //  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A\\TRAAABD128F429CF47.h5"
  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\B\\R\\TRABRMJ128E0780E42.h5"
  // a new hope
  val h5File: H5File = hdf5_getters.hdf5_open_readonly(filePath)

  @Before
  def setup() = {

  }

  @After
  def close() = {
    hdf5_getters.hdf5_close(h5File)
  }

  @Test
  def getDecadeTest() = {
    val year = 2003
    val result = analysis.getDecade(year)

    assert(2000 == result)
  }

  @Test
  def weightedMeanTest() = {
    val values = List(10, 5, 1)
    val weights = List(1.0, .8, .6)

    var total = 0.0
    var div = 0.0
    for (i <- values.indices) {
      total = total + (values(i) * weights(i))
      div = div + weights(i)
    }

    println(total / div)
  }

  @Test
  def findSimilarSectionsTest(): Unit = {
    val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A"
    // gotta loop through files if there's more than one song bro

//    val dir = new File(inputPath)
//    val dirList = Main.getRecursiveListOfFiles(dir)
//    println("Found " + dirList.length + " files.")
//    dirList.foreach(file => analysis.findSimilarSections(hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)))

    analysis.findSimilarSections(hdf5_getters.hdf5_open_readonly(filePath))
  }
}

