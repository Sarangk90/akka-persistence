package akka

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, Offset, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

object PersistenceQueryDemo extends App {

  val system = ActorSystem("persistent-query")

  implicit val mat = ActorMaterializer()(system)

  val queries = PersistenceQuery(system).readJournalFor[CassandraReadJournal](
    CassandraReadJournal.Identifier
  )

  val evts: Source[EventEnvelope, NotUsed] =
    queries.eventsByPersistenceId("persistence-id", 0, Long.MaxValue)

  evts.runForeach { evt => println(s"Event ${evt}") }

  Thread.sleep(10000)

  system.terminate()

}