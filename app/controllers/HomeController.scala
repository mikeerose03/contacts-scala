package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.i18n.{ I18nSupport, MessagesApi }

import ejisan.play.libs.{ PageMetaSupport, PageMetaApi }

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
  val messagesApi: MessagesApi,
  val pageMetaApi: PageMetaApi,
  implicit val wja: WebJarAssets
) extends Controller with I18nSupport with PageMetaSupport {

  /**
   * Create an Action to render an HTML page.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

}
