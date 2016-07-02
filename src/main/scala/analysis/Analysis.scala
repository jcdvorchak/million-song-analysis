package analysis

import msongdb.hdf5_getters
import java.io.{File, PrintWriter}

import scala.collection.JavaConversions._
import java.util

import ncsa.hdf.`object`.h5.H5File
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception

/**
  * Created by jcdvorchak on 7/2/2016.
  */
class Analysis {
  val outputPath = "C:\\Users\\Admin\\Documents\\GitHub\\million-song-analysis\\output\\output.txt"

  def keyTrendByGenreYear(dirList: Array[File]) {
    var h5File: H5File = null
    var stringBuilder: StringBuilder = null
    val lineList = new util.ArrayList[String]()
    dirList.foreach { currFile =>
      //      println(currFile.getAbsolutePath)

      h5File = hdf5_getters.hdf5_open_readonly(currFile.getAbsolutePath)

      stringBuilder = new StringBuilder
      stringBuilder.append(hdf5_getters.get_artist_name(h5File)).append(",")
      stringBuilder.append(hdf5_getters.get_title(h5File)).append(",")
      try {
        stringBuilder.append(hdf5_getters.get_artist_terms(h5File)(0)).append(",")
      } catch {
        case he: HDF5Exception => stringBuilder.append(",")
        case e: Exception => e.printStackTrace()
      }
      stringBuilder.append(hdf5_getters.get_key(h5File)).append(",")
      stringBuilder.append(hdf5_getters.get_key_confidence(h5File)).append(",")
      stringBuilder.append(getDecade(hdf5_getters.get_year(h5File)))
      lineList.add(stringBuilder.toString)

      //      println(lineList.length)


      //      hdf5_getters.get_artist_terms(h5File).foreach(print(_))
      //      println
      //      hdf5_getters.get_artist_terms_freq(h5File).foreach{in =>
      //        print(in)
      //        print(" ")
      //      }
      //      println
      //      hdf5_getters.get_artist_terms_weight(h5File).foreach{in =>
      //        print(in)
      //        print(" ")
      //      }
      //      hdf5_getters.get_artist_mbtags(h5File).foreach{in =>
      //        print(in)
      //        print(" ")
      //      }

      //      pw.write(hdf5_getters.get_artist_name(h5File))
      //      pw.write("," + hdf5_getters.get_title(h5File))
      //      pw.write("," + hdf5_getters.get_artist_terms(h5File)(0))
      //      pw.write("," + hdf5_getters.get_key(h5File))
      //      pw.write("," + hdf5_getters.get_key_confidence(h5File))
      //      pw.write("," + hdf5_getters.get_year(h5File))
      //      pw.println()

      hdf5_getters.hdf5_close(h5File)
    }

    //    pw.flush()
    //    pw.close

    val grouped = lineList.groupBy { line =>
      val lineArr = line.split(",")
      lineArr(3) + "," + lineArr(2) + "," + lineArr(5)
    }

    println("groupBy " + grouped.size)

    val output = grouped.map { pair =>
      pair._1 + "," + pair._2.length
    }

    writeToFile(outputPath, output)

  }

  def attrByGenreDecade(dirList: Array[File]) {
    var h5File: H5File = null

    var stringBuilder: StringBuilder = null
    val lineList = new util.ArrayList[String]()

    dirList.foreach { currFile =>
      //      println(currFile.getAbsolutePath)


      h5File = hdf5_getters.hdf5_open_readonly(currFile.getAbsolutePath)

      //genre(top5diflines),genre_weight,decade,attr1,attr2,attr3,....
      var artistTerms: Array[String] = null
      try {
        artistTerms = hdf5_getters.get_artist_terms(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => artistTerms = new Array[String](0)
      }
      var artistTermWeights: Array[Double] = null
      try {
        artistTermWeights = hdf5_getters.get_artist_terms_weight(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => artistTermWeights = new Array[Double](0)
      }
      var artistTermFreq: Array[Double] = null
      try {
        artistTermFreq = hdf5_getters.get_artist_terms_freq(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => artistTermFreq = new Array[Double](0)
      }
      var year: Int = 0
      try {
        year = hdf5_getters.get_year(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => year = 0
      }
      val decade = getDecade(year)
      var danceability: Double = 0.0
      try {
        danceability = hdf5_getters.get_danceability(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => danceability = -1
      }
      var loudness: Double = 0.0
      try {
        loudness = hdf5_getters.get_loudness(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => loudness = -1
      }
      var tempo: Double = 0.0
      try {
        tempo = hdf5_getters.get_tempo(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => tempo = -1
      }
      var energy: Double = 0.0
      try {
        energy = hdf5_getters.get_energy(h5File)
      } catch {
        case hdf5Exception: HDF5Exception => energy = -1
      }
      //      hdf5_getters.get_key(h5File) // take into account confidence here

      if (artistTerms.length != artistTermWeights.length ||
        artistTerms.length != artistTermFreq.length ||
        artistTermFreq.length != artistTermWeights.length) {
        println("Missing or mismatched artistTerms & artistTermWeights.. skipping line")
      } else {
        // TODO top 5 genre? or the ones above a frequency threshhold and weight?
        var range = 3

        if (artistTerms.length < 3) {
          range = artistTerms.length
        }
        for (i <- 0 until range) {
          if (artistTermFreq(i)>=.7) {
            stringBuilder = new StringBuilder
            stringBuilder.append(artistTerms(i)).append(",")
            stringBuilder.append(artistTermWeights(i)).append(",")
            stringBuilder.append(decade).append(",")
            stringBuilder.append(danceability).append(",")
            stringBuilder.append(loudness).append(",")
            stringBuilder.append(tempo).append(",")
            stringBuilder.append(energy)

            lineList.add(stringBuilder.toString)
          }
        }
      }
    }

    writeToFile(outputPath,lineList)

    val grouped = lineList.groupBy { line =>
      val lineArr = line.split(",")
      lineArr(0) + "," + lineArr(1) + "," + lineArr(2)
    }

    println("groupBy " + grouped.size)

//    val output = grouped.map { pair =>
//      val key = pair._1
//      val lineList = pair._2
//    }
//    writeToFile(outputPath, output)

    hdf5_getters.hdf5_close(h5File)
  }

  def writeToFile(path: String, lineList: Iterable[String]): Unit = {
    val pw = new PrintWriter(new File(path))

    lineList.foreach { line =>
      pw.println(line)
      pw.flush()
    }

    pw.close()
  }

  /*
   * Determine the decade based on the songs release year
   * return -1 if it's an incorrect year format
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
}
