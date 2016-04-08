package services

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import api.{Protocol, UpdateThermostat}
import com.firebase.client._
import upickle.default._

object WebSocketActor {
  def props(token: String, out: ActorRef) = Props(classOf[WebSocketActor], token, out)
}

class WebSocketActor(token: String, out: ActorRef) extends Actor with ActorLogging {
  implicit val _ = ServerSettings(context.system)

  val fb = new FirebaseClient(self).auth(token)

  def receive = LoggingReceive {
    case msg: Protocol => out ! write(msg)

    case ProtocolReader(UpdateThermostat(id, temp)) => fb.updateThermostat(id, temp)

    case fbe: FirebaseError => log.error(s"Got firebase error $fbe")
  }
}
