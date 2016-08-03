package actors

import akka.actor._

class ClientActor(server: ActorRef, out:ActorRef) extends Actor {
	import actors.MessagesHelper._

	override def preStart() = {
		println("...")
		server ! Subscribe
	}

	override def postStop() = {
		server ! Disconnect
	}

	def receive = {
		case msg: String => server ! Message(msg)
		case Message(msg) => 
			println("server: "+msg)
			out ! msg
		case _ => println("client here...")
	}
}

object ClientActor {
	def props(server: ActorRef, out: ActorRef) = Props(classOf[ClientActor], server, out)
}

object MessagesHelper {
	object Subscribe
	object Connect
	object Disconnect
	case class Message(msg: String)
}