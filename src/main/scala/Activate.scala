package activate

import java.sql.Timestamp

import net.fwbrasil.activate.migration.Migration
import net.fwbrasil.activate.storage.memory.TransientMemoryStorage
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.postgresqlDialect

object Benchmarking {
  def time[R](description: String = "standard test", block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block // call-by-name
    val t1 = System.currentTimeMillis()
    println(s"Elapsed time for $description: " + (t1 - t0) + "ms")
    result
  }
}

import net.fwbrasil.activate.ActivateContext


object persistenceContext extends ActivateContext {
  //val storage = new TransientMemoryStorage
  // sync postgresql
  val storage = new PooledJdbcRelationalStorage {
    val jdbcDriver = "org.postgresql.Driver"
    val user = Some("postgres")
    val password = Some("postgres")
    val url = "jdbc:postgresql://127.0.0.1/benchmarking2"
    val dialect = postgresqlDialect
  }

}

import activate.persistenceContext._

object Domain {

  class DbObject extends Entity {
    val lastUpdate = new Timestamp(System.currentTimeMillis())
  }

  case class Award(var name: String) extends DbObject

  case class Author(var firstName: String,
                    var lastName: String,
                    var email: Option[String] = None,
                    var full_content: Option[String] = None) extends DbObject {
    lazy val awards = select[AwardPresentation].where(_.author :== this).map(_.award)
    //    lazy val books = List[Book]()
    val full_name = s"$firstName $lastName"
  }

  case class Book(var title: String, var author: Author, var read: Boolean = false) extends DbObject

  case class AwardPresentation(var award: Award, var author: Author) extends DbObject

  class MyMigration extends Migration {
    def timestamp =  System.currentTimeMillis() //2012111

    def up = {
      removeAllEntitiesTables.ifExists
      createTableForAllEntities
        .ifNotExists
    }
  }

}

import activate.Benchmarking._
import activate.Domain._

object ActivateBenchMark extends App {
  1 to 5 foreach { _ =>
    transactional {
      time("insert statements", {
        1 to 100 foreach { n =>
          val jrrt = new Author("JRR", s"Tolkien$n")
          new Author("Jane", "Austen")
          new Author("Philip", "Pullman", None, Some( """ <xml>Test</xml> """))
        }
        1 to 100 foreach { n =>
          val jrrt = select[Author].where(_.lastName :== s"Tolkien$n").head
          val lord_of_the_rings = Book("The Lord of the Rings", jrrt)
          Book("Pride and Prejudice", jrrt)
          Book("His Dark Materials", jrrt)
          val manBookerPrize = Award("Man Booker Prize")
          val commonwealthBookPrize = Award("Commonwealth Book Prize")
          new AwardPresentation(manBookerPrize, jrrt)
          new AwardPresentation(commonwealthBookPrize, jrrt) //association is missing
        }
      })
    }
    transactional {
      println(s"Number of authors in database: ${all[Author].size }")
      time("select statements", {
        1 to 100 foreach { n =>
          val jrrt = select[Author].where(_.lastName :== s"Tolkien$n").head
          all[Author].map(_.full_name).mkString(",")
          all[Book].filter(_.title.contains("Dark")).map(_.title).mkString(",")
          select[Author].where(_.lastName :== "Pullmann").foreach(a => println(a.full_content))
        }
      })
    }
  }
}