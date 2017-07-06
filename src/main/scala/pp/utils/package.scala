package pp

import java.io.Closeable

import scala.util.Try

/**
  * Created by pavel on 06.07.17.
  */
package object utils {

  def cleanly[A, B](resource: A)(cleanup: A => Any)(code: A => B): B = {
    try {
      code(resource)
    } finally {
      Try(cleanup(resource))
    }
  }

  implicit class Use[T <: Closeable](closable: T) {
    def use[B](code: T => B): B = cleanly(closable)(_.close())(code)
  }

}
