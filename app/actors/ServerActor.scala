package actors

import akka.actor._
import akka.event.LoggingReceive

class ServerActor extends Actor {
	import actors.MessagesHelper._
	
	var users = Set[ActorRef]()

	def receive = {
		case Subscribe => users += sender
		case m: Message => users.foreach(_ ! m)
		case _ => println("No messages received")
	}
}
