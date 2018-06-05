package pp

import scala.util.Try

/**
  * Created by pavel on 06.07.17.
  */
package object utils {

  implicit class Use[T <: AutoCloseable](closable: T) {
    def use[B](code: T => B): B = {
      try {
        code(closable)
      } finally {
        Try(closable.close())
      }
    }
  }
}
