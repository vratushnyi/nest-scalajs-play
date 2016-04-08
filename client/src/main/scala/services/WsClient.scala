package services

import api.{NestDevicesMessage, Protocol, ThermostatUpdated}
import org.scalajs.dom
import org.scalajs.dom.{MessageEvent, WebSocket}
import org.scalajs.dom.raw.{Document, ErrorEvent, Event}
import logger._
import upickle.default._

import language.higherKinds
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object WsClient {

  private[this] var socket: Option[WebSocket] = None

  def connect(): Unit = if (socket.isEmpty) {
    val client = new WebSocket(getWebSocketUri(dom.document))
    client.onopen = { (event: Event) ⇒
      log.info("WebSocket connection established")
      socket = Some(client)
      event
    }
    client.onmessage = { (event: MessageEvent) ⇒
      log.debug(s"WebSocket message received: ${event.data.toString}")
      read[Protocol](event.data.toString) match {
        case msg: NestDevicesMessage => AppCircuit.dispatch(msg)
        case msg: ThermostatUpdated => AppCircuit.dispatch(msg)
        case msg => log.warn(s"Unhandled message $msg")
      }
    }
    client.onerror = { (event: ErrorEvent) ⇒
      log.error(event.message)
      socket = None
    }
    client.onclose = { (event: Event) ⇒
      log.info("WebSocket connection is closed")
      socket = None
    }
  }

  def send(message: Protocol): Future[Unit] = {
    Future {
      socket.foreach(_.send(write(message)))
    }
  }

  private def getWebSocketUri(document: Document): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/subscribe"
  }

}
