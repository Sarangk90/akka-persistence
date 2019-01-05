package akka

import akka.MyPersistenceActor.{Confirm, Msg, _}
import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import akka.persistence.{AtLeastOnceDelivery, PersistentActor, RecoveryCompleted}


class MyPersistenceActor(destination: ActorSelection)
  extends PersistentActor with AtLeastOnceDelivery {

  override def persistenceId: String = "persistence-id"

  override def receiveCommand: Receive = {
    case s: String ⇒ {
      println(s"Client: Received brand new command with message ${s}")
      persist(MsgSent(s))(updateState)
    }
    case Confirm(deliveryId) ⇒ persist(MsgConfirmed(deliveryId))(updateState)
  }

  override def receiveRecover: Receive = {
    case evt: Evt ⇒ {
      println(s"Client: Recovering event ${evt}")
      updateState(evt)
    }
    case RecoveryCompleted => {
      println("Client: Recovery completed successfully and now going to address new messages")
    }
  }

  def updateState(evt: Evt): Unit = evt match {
    case MsgSent(s) ⇒
      deliver(destination)(deliveryId ⇒ Msg(deliveryId, s))

    case MsgConfirmed(deliveryId) ⇒ {
      println(s"Client: Response received with Delivery Id .... ${deliveryId}")
      confirmDelivery(deliveryId)
    }
  }
}

object MyPersistenceActor {

  case class Msg(deliveryId: Long, s: String)

  case class Confirm(deliveryId: Long)

  sealed trait Evt

  case class MsgSent(s: String) extends Evt

  case class MsgConfirmed(deliveryId: Long) extends Evt

}

class MyDestination extends Actor {
  def receive = {
    case Msg(deliveryId, s) ⇒
      println(s"Server: Request received with Delivery Id .... ${deliveryId}")

      sender() ! Confirm(deliveryId)
  }
}

object Test extends App {
  val system = ActorSystem("persistent-actors")

  val receiver = system.actorOf(Props[MyDestination], "destination")
  val actorSelection = system.actorSelection("/user/destination")
  val sender = system.actorOf(Props(classOf[MyPersistenceActor], actorSelection), "ActorWithALOD")

  sender ! "Hello"

  Thread.sleep(10000)
  system.terminate()
}

