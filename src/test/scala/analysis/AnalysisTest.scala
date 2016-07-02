package analysis

import org.junit.{Before, Test}

/**
  * Created by jcdvorchak on 7/2/2016.
  */
@Test
class AnalysisTest {
  val analysis: Analysis = new Analysis

  @Before
  def setup() = {

  }

  @Test
  def getDecadeTest() = {
    val year = 2003
    val result = analysis.getDecade(year)

    assert(2000 == result)
  }

  @Test
  def weightedMeanTest() = {
    val values = List(10,5,1)
    val weights = List(1.0,.8,.6)

    var total = 0.0
    var div = 0.0
    for (i <- values.indices) {
      total = total+(values(i)*weights(i))
      div = div+weights(i)
    }

    println(total/div)
  }
}
