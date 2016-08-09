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


//import ejisan.play.data.Forms._

@Singleton
class AuthenticationController @Inject() (
	val messagesApi: MessagesApi,
	val users: Users,
	implicit val wja: WebJarAssets
) extends Controller with I18nSupport {

	val registerForm = Form(
		mapping(
			"userID" -> optional( number ),
			"username" -> nonEmptyText,
			"password" -> nonEmptyText
		)( User.apply )( User.unapply )
	)

	def login = Action.async { implicit request =>
		Future.successful( Ok( views.html.login( loginForm, registerForm ) ) )
	}

	val loginForm = Form(
		mapping(
			"userID" -> optional( number ),
			"username" -> nonEmptyText,
			"password" -> nonEmptyText
		)( User.apply )( User.unapply )
	)

	def loginAction = Action.async { implicit request => 
		loginForm.bindFromRequest.fold(
			{
				error => Future.successful( BadRequest( views.html.login( error, registerForm ) ).flashing( "msg" -> "Username or password is incorrect", "color" -> "alert" ) )
			}, {
				user => {
					users.validatePass( user.username, user.password ) map {
						case Some( user ) => Redirect( routes.GirlController.isLogged ).withSession( "userid" -> user.userID.get.toString, "username" -> user.username )
						case None => Redirect( routes.AuthenticationController.login ).flashing( "msg" -> "Username or password is incorrect", "color" -> "alert" )
					}	
				}
			}
		)
	}

	def logout = Action { implicit request => 
		Ok( views.html.login( loginForm,registerForm ) ).withNewSession
	}


	def registerAction = Action.async { implicit request => 
		registerForm.bindFromRequest.fold(
			 error => Future.successful( BadRequest( views.html.login( loginForm, error ) ) ), 
			 user => {
				users.insertUser( user ) map { 
					flag => 
					if( flag )
						Redirect( routes.AuthenticationController.login ).flashing( "msg"-> "Register Successful. You may now log in.", "color" -> "success" )
					else Ok( "Error" )
				}
			}	
		)
	}

}