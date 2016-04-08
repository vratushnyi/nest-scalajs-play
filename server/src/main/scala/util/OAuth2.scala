package util

import play.api.{Application, Play}
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger

class OAuth2(application: Application) {
  lazy val clientAuthId = application.configuration.getString("nest.client.id").get
  lazy val clientAuthSecret = application.configuration.getString("nest.client.secret").get
  lazy val baseUrl = application.configuration.getString("nest.oauth.base.url").get
  lazy val accessTokenUrl = application.configuration.getString("nest.oauth.access_token.url").get

  def getAuthorizationUrl(redirectUri: String, state: String): String = {
    baseUrl.format(clientAuthId, redirectUri, state)
  }

  def getToken(code: String): Future[String] = {
    val tokenResponse = WS.url(accessTokenUrl)(application).
      withQueryString("client_id" -> clientAuthId,
        "client_secret" -> clientAuthSecret,
        "code" -> code,
        "grant_type" -> "authorization_code").
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(Results.EmptyContent())

    tokenResponse.flatMap { response =>
      (response.json \ "access_token").asOpt[String].fold(Future.failed[String](new IllegalStateException("Sod off!"))) { accessToken =>
        Future.successful(accessToken)
      }
    }
  }
}

object OAuth2 extends Controller {
  lazy val oauth2 = new OAuth2(Play.current)

  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async { implicit request =>
    (for {
      code <- codeOpt
      state <- stateOpt
      oauthState <- request.session.get("oauth-state")
    } yield {
      if (state == oauthState) {
        oauth2.getToken(code).map { accessToken =>
          Redirect(util.routes.OAuth2.success()).withSession("oauth-token" -> accessToken)
        }.recover {
          case ex: IllegalStateException => Unauthorized(ex.getMessage)
        }
      }
      else {
        Future.successful(BadRequest("Invalid nest login"))
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  def success() = Action { request =>

    implicit val app = Play.current
    request.session.get("oauth-token").fold(Unauthorized("No way Jose")) { authToken =>
      Redirect(controllers.routes.Application.index)
    }
  }
}