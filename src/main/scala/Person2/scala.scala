package Person2

import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.StorageFactory
import net.fwbrasil.activate.storage.memory.TransientMemoryStorage

object persistenceContext extends ActivateContext {
  val storage = new TransientMemoryStorage
}

import persistenceContext._

object domain {

  class Person(var name: String) extends Entity

  class CreatePersonTableMigration extends Migration {
    def timestamp = 2012111

    def up = {
      table[Person]
        .createTable(
          _.column[String]("name"))
    }
  }

}

import domain._

object simpleMain extends App {

  transactional {
    new Person("John")
  }

  val john = transactional {
    select[Person].where(_.name :== "John").head
  }

  transactional {
    john.name = "John Doe"
  }

  transactional {
    all[Person].foreach(_.delete)
  }
}