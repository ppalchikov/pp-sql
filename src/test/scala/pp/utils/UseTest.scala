package pp.utils

import java.nio.file.{Files, Paths}

import org.scalatest.FunSuite
import scala.collection.JavaConverters._


/**
  * Created by pavel on 06.07.17.
  */
class UseTest extends FunSuite {

  test("test use") {
    Files.newBufferedReader(Paths.get("LICENSE")).use { r =>
      println(r.lines().iterator().asScala.mkString(" "))
      r.close()
    }
  }

}
