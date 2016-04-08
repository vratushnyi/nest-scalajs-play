package controllers

import java.util.UUID

import play.api.Play
import play.api.mvc._
import services.WebSocketActor
import util.OAuth2
import play.api.Play.current

import scala.concurrent.Future

object Application extends Controller {

  def index = Action { implicit request =>
    request.session.get("oauth-token") match {
      case Some(_) => Ok(views.html.index("Nest scala.js play demo"))
      case None =>
        val oauth2 = new OAuth2(Play.current)
        val callbackUrl = util.routes.OAuth2.callback(None, None).absoluteURL()
        val state = UUID.randomUUID().toString  // random confirmation string
        val redirectUrl = oauth2.getAuthorizationUrl(callbackUrl, state)
        Redirect(redirectUrl).withSession("oauth-state" -> state)
    }
  }

  def subscribe = WebSocket.tryAcceptWithActor[String, String] { req =>
    Future.successful(req.session.get("oauth-token") match {
      case None => Left(Forbidden)
      case Some(token) => Right(WebSocketActor.props(token, _))
    })
  }

  def logging = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}
