package akka


import akka.actor.{Actor, ActorSystem, PoisonPill, Props}


// We create a new Actor that just prints out what it processes
class UseMailbox extends Actor {

  def receive = {
    case x ⇒ println(x)
  }
}

object UseMailbox extends App {

  val system = ActorSystem("Custom-mailbox")
  val a = system.actorOf(Props[UseMailbox], "priomailboxactor")

  a ! 'lowpriority
  a ! 'lowpriority
  a ! 'highpriority
  a ! 'pigdog
  a ! 'pigdog2
  a ! 'pigdog3
  a ! 'highpriority
  a ! PoisonPill

}

