package actors

import akka.actor._
import akka.event.LoggingReceive

class ServerActor extends Actor {
	import MessagesHelper._
	
	var users = scala.collection.mutable.ListBuffer.empty[(String, ActorRef)]

	def receive = {
		case Subscribe(username) => users += ((username, sender))
									users.foreach(r => r._2 ! Connect(username))
		case Disconnect(username) => users -= ((username, sender))
									 users.foreach(r => r._2 ! Disconnect(username))
		case m: Message => users.foreach(_._2 ! m)
		case _ => println("No messages received")
	}
}

object MessagesHelper {
	case class Subscribe(username: String)
	case class Connect(username: String)
	case class Disconnect(username: String)
	case class Message(msg: String, username: String, msgType: String)
}
