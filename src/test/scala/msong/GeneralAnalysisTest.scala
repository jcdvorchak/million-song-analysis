package msong

import org.junit.Test

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._


/**
  * Created by jcdvorchak on 7/7/2016.
  */
@Test
class GeneralAnalysisTest {
  val lineList = new ListBuffer[String]

  lineList.add("The Box Tops|Soul Deep|1969|Memphis, TN|0.0|blue-eyed soul|||19s-38s|19.311109999999996|50s-1m10s|19.311109999999996|0.7055871132981913|0.3241946665994271|0.9921597797511871|0.9979073599904491|0.9505720645419213|0.8528977758429743|0.9609732676711117")
  lineList.add("Sonora Santanera|Amor De Cabaret|0||0.0|salsa|cumbia|tejano|1m8s-1m38s|30.256739999999994|2m11s-2m49s|30.256739999999994|0.6126432807461318|0.7394064419475623|0.9979817765615484|0.9988077484940231|0.9425467409499226|0.5874658967507103|0.9006621381395529")
  lineList.add("Adam Ant|Something Girls|1982|London, England|0.0|pop rock|new wave||27s-55s|27.175110000000004|1m7s-1m38s|27.175110000000004|0.6555972392054815|0.47945055384728674|0.9916089058400303|0.9988276837424916|0.9467737236617626|0.600072018195013|0.9470292188961188")
  lineList.add("Adam Ant|Something Girls|1982|London, England|0.0|pop rock|new wave||27s-55s|27.175110000000004|2m5s-2m32s|27.175110000000004|0.7719565346328036|0.7017120418868747|0.9934048259654351|0.999205250620156|0.9690189139539026|0.8057100177992291|0.9851501432606065")
  lineList.add("Adam Ant|Something Girls|1982|London, England|0.0|pop rock|new wave||1m7s-1m38s|31.1794|2m5s-2m32s|31.1794|0.6518770266311141|0.4525214210507246|0.994703238464922|0.9992063314453855|0.9461072256696225|0.6601704044727658|0.952784177047085")
  lineList.add("Adam Ant|Something Girls|1982|London, England|0.0|pop rock|new wave||1m7s-1m38s|31.1794|3m8s-3m45s|31.1794|0.6479823147309582|0.5330689172898093|0.9910176553326483|0.999245967185233|0.9479915922186617|0.6330152693100572|0.9599367346680043")
  lineList.add("Tweeterfriendly Music|Drop of Rain|0|Burlington, Ontario, Canada|0.0|post-hardcore|screamo|emo|40s-57s|16.64797|1m59s-2m19s|16.64797|0.5594222942754565|0.6096653672733008|0.9917312652385524|0.9959933679009152|0.9186122475104809|0.6293401541511798|0.9635549561538058")

  @Test
  def sectionSimilarityCountGenreTest(): Unit = {
    val result = GeneralAnalysis.sectionSimilarityCountGenre(lineList.toList)

    result.foreach(println)
  }

  @Test
  def sectionSimilarityCountDecadeTest(): Unit = {
    val result = GeneralAnalysis.sectionSimilarityCountDecade(lineList.toList)

    result.foreach(println)
  }

  @Test
  def sectionSimilarityCountLocationTest(): Unit = {
    val result = GeneralAnalysis.sectionSimilarityCountLocation(lineList.toList)

    result.foreach(println)
  }
}
