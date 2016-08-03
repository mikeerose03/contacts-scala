package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.data._
import play.api.data.Forms._

import models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor._
import play.api.libs.streams._
import akka.stream._

import actors._

//import ejisan.play.data.Forms._

@Singleton
class GirlController @Inject() (
	val messagesApi: MessagesApi,
	val users: Users,
	val contacts: Contacts,

	implicit val system: ActorSystem,
	implicit val materializer: Materializer,

	implicit val wja: WebJarAssets
) extends Controller with I18nSupport {


	def isLogged = Action.async { implicit request => 

		if( !request.session.get( "userid" ).isEmpty ){
			Future.successful( Redirect( routes.GirlController.logged ).flashing( "msg" -> "successfully logged in", "color" -> "success" ) )
			} else {
				Future.successful( Redirect( routes.AuthenticationController.login ).flashing( "msg" -> "Something went wrong", "color" -> "alert" ) )
			}
	}

	val contactForm = Form(
		mapping(
			"contactID" -> optional( number ),
			"username" -> nonEmptyText,
			"number" -> nonEmptyText,
			"userID" -> number
		)( Contact.apply )( Contact.unapply )
	)


	def removeContact( id: Int ) = Action.async{ implicit request => 
		contacts.deleteContact( id ) map{
			_ => Redirect( routes.GirlController.logged ).flashing( "msg" -> "Contact Deleted", "color" -> "success" )
		}
	}


	def logged = Action.async { implicit request => 
		val userid = request.session.get( "userid" ).getOrElse( "0" ).toInt
		val username = request.session.get( "username" ).getOrElse( "User" )

			contacts.allContacts map {
				results => {
					Ok( views.html.logged( contactForm, results.filter( _.userID == userid ), userid, username ) )
				}
			}
	}

	def updateContact( id: Int ) = Action.async { implicit request => 
		val user = request.session.get( "userid" ).getOrElse( "0" ).toInt
		contacts.findById( id ) map{
			case Some( contact ) => Ok( views.html.update( contactForm.fill( contact ), user ) )
			case None => Redirect( routes.GirlController.logged ).flashing( "msg" -> "Something Went Wrong. Contact Not Found", "color" -> "alert" )
		}
	}

	def updateAction = Action.async{ implicit request => 
		contactForm.bindFromRequest.fold(
			error => { 
				Future.successful( Redirect( routes.GirlController.logged ).flashing( "msg" -> "Something went wrong", "color" -> "alert" ) ) 
			},
			{ contact => 
				contacts.updateContact( contact ) map {
					case true => Redirect( routes.GirlController.logged ).flashing( "msg"-> "Contact Updated", "color" -> "success" ) 
					case false => Ok( views.html.test( contact ) )
				}
			}
		)
	}

	def insertContactAction = Action.async { implicit request => 
		val loggedUser = request.session.get( "userID" ).getOrElse( "0" ).toInt

		contactForm.bindFromRequest.fold(
			error => { 
				Future.successful( Redirect ( routes.GirlController.logged ).flashing( "msg" -> "Please enter fields", "color" -> "alert" ) )
			},
			{ contact => {
				contacts.insertContact( contact ) map {
					flag =>
						if( flag )
							Redirect( routes.GirlController.logged ).flashing( "msg"-> "Contact Added", "color" -> "success" )
						else Redirect( routes.GirlController.logged ).flashing( "msg"-> "Adding Contact Unsuccessful. Something went wrong", "color" -> "alert" )
					}
				}
			}
		)
	}

	def chat = Action { implicit request => 
		Ok(views.html.chat())
	}

	val s = ActorSystem("test")
	val server = s.actorOf(Props[ServerActor], "server")

	def ws = WebSocket.accept[String, String]{ implicit request =>
		ActorFlow.actorRef(out => ClientActor.props(server, out))
	} 

}

//to be continued





