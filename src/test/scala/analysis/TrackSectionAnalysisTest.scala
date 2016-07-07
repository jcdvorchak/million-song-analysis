package analysis

import java.io.{File, FileInputStream}
import java.util

import hdf5Parser.MSongHDF5Parser
import msongdb.hdf5_getters
import org.junit.{Before, Test}
import ncsa.hdf.`object`.h5.H5File
import org.apache.commons.io.IOUtils

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
    val inputPath = "C:\\Users\\Admin\\Downloads\\MillionSongSubset\\data\\A\\A\\A"
    val fileContent = IOUtils.toByteArray(this.getClass.getResourceAsStream("/TRAAAAW128F429D538.h5"))
    // gotta loop through files if there's more than one song bro

    val file = new Array[File](1)
    file(0) = new File(filePath)

    val dir = new File(inputPath)
    val dirList = Main.getRecursiveListOfFiles(dir)
    var missCount, hitCount, brokenCount, totalCount = 0.0
    val genreHitMap, genreMissMap = new util.TreeMap[String, Int]
    val decadeHitMap, decadeMissMap = new util.TreeMap[String, Int]
    var termsFreq, termsWeight: Array[Double] = null
    println("Found " + dirList.length + " files.")
    dirList.foreach { file =>
      //      val h5File = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath)

      try {
        //        if (hdf5_getters.get_artist_terms(h5File).take(5).contains("rock")) {
        //        val track = MSongHDF5Parser.readHDF5File(fileContent)
        val track = MSongHDF5Parser.readHDF5File(IOUtils.toByteArray(new FileInputStream(file)))
        val trackAnalysis = new TrackSectionAnalysis(track)
        val result = trackAnalysis.findSimilarSections()

        termsFreq = track.getArtistTermsFreq
        termsWeight = track.getArtistTermsWeight

        if (result == "") {
          missCount += 1

          try {
            var termCount = 0
            track.getArtistTerms.foreach { term =>
              if (termsWeight(termCount) > 0.9 && termsFreq(termCount) > 0.9) {
                if (!genreMissMap.containsKey(term))
                  genreMissMap.put(term, 1)
                else
                  genreMissMap.put(term, genreMissMap.get(term) + 1)
              }

              termCount += 1
            }

            val decade = analysis.getDecade(track.getYear.toInt).toString
            if (!decadeMissMap.containsKey(decade))
              decadeMissMap.put(decade, 1)
            else
              decadeMissMap.put(decade, decadeMissMap.get(decade) + 1)
          } catch {
            case e: Exception =>
          }
        } else if (result == "broken") {
          brokenCount += 1
        } else {
          println(result)
          hitCount += 1

          try {
            var termCount = 0
            track.getArtistTerms.foreach { term =>
              if (termsWeight(termCount) > 0.9 && termsFreq(termCount) > 0.9) {
                if (!genreHitMap.containsKey(term))
                  genreHitMap.put(term, 1)
                else
                  genreHitMap.put(term, genreHitMap.get(term) + 1)
              }

              termCount += 1
            }

            val decade = analysis.getDecade(track.getYear.toInt).toString
            if (!decadeHitMap.containsKey(decade))
              decadeHitMap.put(decade, 1)
            else
              decadeHitMap.put(decade, decadeHitMap.get(decade) + 1)
          } catch {
            case e: Exception =>
          }

        }
        totalCount += 1
        //        }
      } catch {
        case e: Exception =>
      }


    }
    println("tracks broken: " + brokenCount)
    println("tracks missed: " + missCount)
    println("tracks hit: " + hitCount)
    println("hit ratio: " + hitCount / totalCount)
    println

    print("genre hits: ")
    genreHitMap.toList.sortWith(_._2 > _._2).take(50).toArray.foreach(print(_) + " ")
    println
    print("genre misses: ")
    genreMissMap.toList.sortWith(_._2 > _._2).take(50).toArray.foreach(print(_) + " ")
    println
    println
    print("decade hits: ")
    decadeHitMap.toList.sortWith(_._2 > _._2).foreach(print(_) + " ")
    println
    print("decade misses: ")
    decadeMissMap.toList.sortWith(_._2 > _._2).foreach(print(_) + " ")

    //    analysis.findSimilarSections(hdf5_getters.hdf5_open_readonly(filePath))

    //    val h5File = hdf5_getters.hdf5_open_readonly(filePath)
    //    val trackAnalysis = new TrackSectionAnalysis(h5File)
    //    println(trackAnalysis.findSimilarSections())
    //    hdf5_getters.hdf5_close(h5File)
  }

}
