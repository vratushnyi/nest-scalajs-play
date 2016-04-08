package services

import api.Protocol
import upickle.default._
import scala.util.Try

object ProtocolReader {
  def unapply(msg: String): Option[Protocol] = Try(read[Protocol](msg)).toOption
}
