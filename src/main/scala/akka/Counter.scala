package com.packt.akka

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence._
import com.packt.akka.Counter.{Cmd, Decrement, Increment}

object Counter {

  sealed trait Operation {
    val count: Int
  }

  case class Increment(override val count: Int) extends Operation

  case class Decrement(override val count: Int) extends Operation

  case class Cmd(op: Operation)

  case class Evt(op: Operation)

  case class State(count: Int)

}


class Counter extends PersistentActor with ActorLogging {

  import Counter._

  override def persistenceId = "counter-example"

  var state: State = State(count = 0)

  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(count)) =>
      state = State(count = state.count + count)
    case Evt(Decrement(count)) =>
      state = State(count = state.count - count)
  }

  val receiveRecover: Receive = {
    case evt: Evt =>
      println(s"Counter receive ${evt} on recovering mood")
      updateState(evt)
    case RecoveryCompleted =>
      println(s"Recovery Complete and Now I'll swtich to receiving mode :)")

  }
  // Persistent receive on normal mood
  val receiveCommand: Receive = {
    case cmd@Cmd(op) =>
      println(s"akka.Counter receive ${cmd}")
      persist(Evt(op)) { evt =>
        updateState(evt)
      }

    case "print" =>
      println(s"The Current state of counter is ${state}")
  }
}

object TestCounter extends App {

  val system = ActorSystem("persistent-actors")

  val counter = system.actorOf(Props[Counter], "counter-system")

  counter ! Cmd(Increment(3))

  counter ! Cmd(Increment(5))

  counter ! Cmd(Decrement(3))

  counter ! "print"

  Thread.sleep(5000)
  system.terminate()

}








