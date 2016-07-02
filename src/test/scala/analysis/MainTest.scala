package analysis

import org.junit._

@Test
class MainTest {

  @Before
  def setup() = {

  }

  @Test
  def testMain() = {
    val args = new Array[String](1)
//    args(0)=authToken

    Main.main(args)
  }
}
