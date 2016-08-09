package modules

import play.api.libs.concurrent.AkkaGuiceSupport
import com.google.inject.AbstractModule
import actors.ServerActor

class ServerActorModule extends AbstractModule with AkkaGuiceSupport {
	def configure = {
		bindActor[ServerActor]("server-actor")
	}
}