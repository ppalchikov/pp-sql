package pp.sql

import java.sql.{Connection, SQLException}

import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource
import org.scalatest.FunSuite
import pp.utils.Use

/**
  * Created by pavel on 06.07.17.
  */
class SqlTest extends FunSuite {

  implicit val dataSource: DataSource = {
    val ds = new JdbcDataSource
    ds.setURL("jdbc:h2:mem:test")
    ds.setUser("sa")
    ds.setPassword("sa")
    ds
  }

  test("transaction") {
    val n = transaction { implicit tx ⇒
      select("select 1")
    }
    assert(n == 1)
  }

  test("transaction before") {
    println("ok")
    implicit val before: BeforeCommit = c ⇒ println("before: " + c)
    val n = transaction { implicit tx ⇒
      select("select 1")
    }
    assert(n == 1)
  }

  test("transaction default error handler") {
    intercept[SQLException] {
      transaction { implicit tx ⇒
        select("select")
      }
    }
  }

  test("session batch") {
    transaction { ses ⇒
      for (i ← 1 to 10) {
        for (j ← 1 to 3) {
          ses.addBatch(s"select $i" → Array(i.asInstanceOf[Object], j.asInstanceOf[Object]))
        }
      }
    }
  }

  private def select(query: String)(implicit tx: Session): Any = {
    tx.connection().prepareStatement(query).use { ps ⇒
      ps.executeQuery().use { rs ⇒
        if (rs.next()) rs.getObject(1)
      }
    }
  }
}
