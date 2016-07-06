package analysis

import java.io.File
import java.util

import msongdb.hdf5_getters
import org.junit.{Before, Test}
import ncsa.hdf.`object`.h5.H5File

import scala.collection.JavaConversions._

/**
  * Created by jcdvorchak on 7/3/2016.
  */
@Test
class TrackSectionAnalysisTest {

  object Break extends Exception {}

  // used for breaking out loops

  val analysis: Analysis = new Analysis

    val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A\\TRAAABD128F429CF47.h5"
//  val filePath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\B\\R\\TRABRMJ128E0780E42.h5"
  // a new hope
  val h5File: H5File = hdf5_getters.hdf5_open_readonly(filePath)

  @Before
  def setup() = {

  }

  @Test
  def findSimilarSectionsTest(): Unit = {
    val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\"
    // gotta loop through files if there's more than one song bro

    val file = new Array[File](1)
    file(0) = new File(filePath)

    val dir = new File(inputPath)
    val dirList = Main.getRecursiveListOfFiles(dir)
    var missCount, hitCount, brokenCount, totalCount = 0.0
    val genreHitMap = new util.TreeMap[String, Int]
    val decadeHitMap = new util.TreeMap[String, Int]
    println("Found " + dirList.length + " files.")
    dirList.foreach { file =>
      val h5File = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)

      try {
//        if (hdf5_getters.get_artist_terms(h5File).take(5).contains("rock")) {
          val trackAnalysis = new TrackSectionAnalysis(h5File)
          val result = trackAnalysis.findSimilarSections()

          try {
            var termCount = 0
            hdf5_getters.get_artist_terms(h5File).foreach { term =>
              if (termCount <= 5 && hdf5_getters.get_artist_terms_freq(h5File)(termCount).toDouble > 0.9) {
                if (!genreHitMap.containsKey(term))
                  genreHitMap.put(term, 1)
                else
                  genreHitMap.put(term, genreHitMap.get(term) + 1)
              }

              termCount += 1
            }

            val decade = analysis.getDecade(hdf5_getters.get_year(h5File)).toString
            if (!decadeHitMap.containsKey(decade))
              decadeHitMap.put(decade, 1)
            else
              decadeHitMap.put(decade, decadeHitMap.get(decade) + 1)
          } catch {
            case e: Exception =>
          }

          if (result == "") {
            missCount += 1
          } else if (result == "broken") {
            brokenCount += 1
          } else {
            println(result)
            hitCount += 1
          }
          totalCount += 1
//        }
      } catch {
        case e: Exception =>
      }

      hdf5_getters.hdf5_close(h5File)
    }
    println("tracks broken: " + brokenCount)
    println("tracks missed: " + missCount)
    println("tracks hit: " + hitCount)
    println("hit ratio: " + hitCount / totalCount)
    println

    print("genre counts: ")
    genreHitMap.toList.sortWith(_._2 > _._2).take(50).toArray.foreach(print(_) + " ")
    println
    println
    print("decade counts: ")
    decadeHitMap.toList.sortWith(_._2 > _._2).foreach(print(_) + " ")

    //    analysis.findSimilarSections(hdf5_getters.hdf5_open_readonly(filePath))

    //    val h5File = hdf5_getters.hdf5_open_readonly(filePath)
    //    val trackAnalysis = new TrackSectionAnalysis(h5File)
    //    println(trackAnalysis.findSimilarSections())
    //    hdf5_getters.hdf5_close(h5File)
  }

}
