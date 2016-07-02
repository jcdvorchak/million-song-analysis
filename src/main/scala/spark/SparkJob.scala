package spark

class SparkJob {

	def main (args: String[]): Unit = {
		
	}

	def weightGenreDecade() {
		val input = sc.textFile("swift://spark-output.spark/output.txt")

		val sqlContext = new org.apache.spark.sql.SQLContext(sc)
		import sqlContext.implicits._

		case class KeyGenre(key: String, genre: String, year: String)
		val key = input.map(line => KeyGenre(line.split(",")(3),line.split(",")(2),line.split(",")(5))).toDF
	
		case class GenreDecade(genre: String, genreWeight: Double, decade: Int, danceability: Double, loudness: Double, tempo: Double, energy: Double)
		val df = input.map{ line => 
		    val lineArr = line.split(",")
		    GenreDecade(lineArr(0),lineArr(1).toDouble,lineArr(2).toInt,lineArr(3).toDouble,lineArr(4).toDouble,lineArr(5).toDouble,lineArr(6).toDouble)                    
		}.toDF

		var genreDecade = input.map{line => 
		    val lineArr = line.split(",")
		    ((lineArr(0),lineArr(2)),(lineArr(1),lineArr(3),lineArr(4),lineArr(5),lineArr(6)))
		}

		genreDecade = genreDecade.filter(pair=> !pair._1._2.toInt.equals(-1))

		// can i do this in a reduce?
		val agg = genreDecade.groupByKey.map{keyval =>
		    val key = keyval._1
		    val valueList = keyval._2
		    println(key)
		    println(valueList)
		    
		    var dTot,lTot,tTot,eTot: Double = 0.0
		    var div: Double = 0.0
		    var weight: Double = 0.0
		    
		    valueList.foreach{record =>
		        weight = record._1.toDouble
		        div = div+weight
		        
		        dTot = dTot+(record._2.toDouble*weight)
		        lTot = lTot+(record._3.toDouble*weight)
		        tTot = tTot+(record._4.toDouble*weight)
		        eTot = eTot+(record._5.toDouble*weight)
		    }
		    
		    (key._1+","+key._2+","+(dTot/div)+","+(lTot/div)+","+(tTot/div)+","+(eTot/div))
		}

		agg.coalesce(1).saveAsTextFile("swift://spark-output.spark/weightedmean.txt")
	
	}
}