package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.i18n.{ I18nSupport, MessagesApi }


@Singleton
class MainController @Inject() (
	val messagesApi: MessagesApi
) extends Controller with I18nSupport { 

	// def index = Action { implicit request => 
	// 	if(request.session.get("username").isDefined)
	// 		Ok
	// 	else
	// 		Redirect(routes.GirlController.index).flashing("msg" -> "access not allowed")		
	// }	

	// def logout = Action { implicit request => 
	// 	Redirect(routes.GirlController.index).flashing("msg" -> "logout")
	// }
}