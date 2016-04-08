package app

import logger._
import components.{GlobalStyles, MainMenu, NestDevicesComponent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import services.{AppCircuit, WsClient}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("Main")
object Main extends js.JSApp {
  sealed trait Loc
  case object MainLoc extends Loc
  case object DevicesLoc extends Loc

  val baseUrl = BaseUrl(dom.window.location.href.takeWhile(_ != '#'))

  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    (emptyRule
      | staticRoute(root, MainLoc) ~> redirectToPage(DevicesLoc)(Redirect.Replace)
      | staticRoute("#devices", DevicesLoc) ~> render(AppCircuit.connect(_.devices)(NestDevicesComponent(_))))
      .notFound(redirectToPage(MainLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(MainMenu(c, r.page),
      <.div(^.className := "container", r.render())
    )
  }

  val router: ReactComponentU[Unit, Resolution[Loc], Any, TopNode] = Router(baseUrl, routerConfig.logToConsole)()

  @JSExport
  def main(): Unit = {
    log.warn("Starting application")
    GlobalStyles.addToDocument()
    ReactDOM.render(router, dom.document.getElementById("root"))
    WsClient.connect()
  }
}