package com.packt.akka

import akka.actor.ActorLogging
import akka.persistence._

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

  println("Starting ........................")

  // Persistent Identifier
  override def persistenceId = "counter-example"

  var state: State = State(count = 0)

  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(count)) =>
      state = State(count = state.count + count)
    case Evt(Decrement(count)) =>
      state = State(count = state.count - count)
  }

  // Persistent receive on recovery mood
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
      println(s"Counter receive ${cmd}")
      persist(Evt(op)) { evt =>
        updateState(evt)
      }

    case "print" =>
      println(s"The Current state of counter is ${state}")

  }


  //  override def recovery = Recovery.none

}








