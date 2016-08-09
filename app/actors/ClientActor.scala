package actors

import akka.actor._
import play.api.libs.json._

class ClientActor(username: String, out:ActorRef) extends Actor {
	import MessagesHelper._

	val server = context.actorSelection("akka://application/user/server-actor")

	override def preStart() = {
		println(".....")
		server ! Subscribe(username)
	}

	override def postStop() = {
		server ! Disconnect(username)
	}

	def receive = {
		case msg: String => server ! msg
		case js: JsValue => var msg = (js \ "message").as[String]
												var msgType = ( js \ "type").as[String]
												var username = (js \ "username").as[String]
											  server ! Message(msg, username, msgType)

		case Message(msg, username, msgType) => 
			println("server: " + msg)
			out ! Json.obj("message" -> msg, "username" -> username, "type" -> msgType)

		case Connect(username) => out ! Json.obj("message" -> s"$username is online", "username" -> "server", "type" -> "con")
		case Disconnect(username) => out ! Json.obj("message" -> s"$username is offline", "username" -> "server", "type" -> "con")
		case _ => println("no messages received")
	}

}

object ClientActor {
	def props(username: String, out: ActorRef) = Props(classOf[ClientActor], username, out)
}


// val msg = (js \ "message").as[String] 