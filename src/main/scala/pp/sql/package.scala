package pp

import java.sql.Connection
import java.util
import java.util.concurrent.atomic.AtomicLong

import com.typesafe.scalalogging.Logger
import javax.sql.DataSource
import pp.utils.Use

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
  * Created by pavel on 06.07.17.
  */
package object sql {
  type ExceptionHandler = Throwable ⇒ Unit
  type BeforeCommit = Session ⇒ Unit

  private val log = Logger("pp.sql")

  def transaction[T, B](code: Session ⇒ T)(implicit dataSource: DataSource, before: BeforeCommit = defaultBeforeCommit, onFailure: ExceptionHandler = defaultExceptionHandler): T = {
    try {
      JdbcSession(dataSource).use { tx ⇒
        Try(code(tx)) match {
          case Success(res) ⇒
            before(tx)
            tx.commit()
            res
          case Failure(ex) ⇒
            tx.rollback()
            throw ex
        }
      }
    } catch {
      case ex: Throwable ⇒
        onFailure(ex)
        throw ex
    }
  }


  private def defaultExceptionHandler(e: Throwable): Unit = {
    log.error(e.getMessage, e)
  }

  private def defaultBeforeCommit(tx: Session): Unit = {
    log.info(s"run before commit $tx")
  }

  trait Session extends AutoCloseable {
    val id: Long

    def commit(): Unit

    def rollback(): Unit

    def connection(): Connection

    def addBatch(batch:(String, Array[Object]))

    def flush()
  }

  class JdbcSession(dataSource: DataSource) extends Session {

    private val con: Connection = dataSource.getConnection
    con.setAutoCommit(false)
    log.info(s"start $this")

    override val id = JdbcSession.generator.incrementAndGet()

    private val batches = mutable.ArrayBuffer.empty[(String, Array[Object])]

    override def commit(): Unit = {
      flush()
      con.commit()
      log.info(s"commit $this")
    }

    override def rollback(): Unit = {
      con.rollback()
      log.info(s"rollback $this")
    }

    override def close(): Unit = {
      Try(con.close())
      log.info(s"close $this")
    }

    override def connection(): Connection = con

    override def toString: String = s"session: $id"

    override def addBatch(batch: (String, Array[Object])): Unit = batches += batch

    override def flush(): Unit = println(batches.groupBy(_._1).mapValues(_.map( a ⇒ util.Arrays.toString(a._2))))
  }

  object JdbcSession {
    private[JdbcSession] val generator: AtomicLong = new AtomicLong()

    def apply(dataSource: DataSource): JdbcSession = new JdbcSession(dataSource)
  }

}
