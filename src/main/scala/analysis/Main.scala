package analysis

import msongdb.hdf5_getters

/**
  * Created by jcdvorchak on 6/30/2016.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val inputPath = "/resources/TRAXLZU12903D05F94.h5"
    val args = new Array[String](1)
    args(0)=inputPath

    val file = hdf5_getters.hdf5_open_readonly(inputPath)
    hdf5_getters.main(args)

//    print("the file: "+file.toString)
//
//    print(hdf5_getters.)



  }

}
