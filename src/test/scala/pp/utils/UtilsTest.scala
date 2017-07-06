package pp.utils

import java.nio.file.Files.newBufferedReader
import java.nio.file.Paths.get

import org.scalatest.FunSuite

import scala.collection.JavaConverters._

/**
  * Created by pavel on 06.07.17.
  */
class UtilsTest extends FunSuite {

  test("test cleanly") {
    cleanly(newBufferedReader(get("LICENSE")))(_.close()) { in =>
      in.lines().iterator().asScala.mkString(" ")
    }
    newBufferedReader(get("LICENSE")).use { r =>
      println(r.lines().iterator().asScala.mkString(" "))
    }
  }

}
