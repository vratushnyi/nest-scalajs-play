package services

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

class ServerSettingsImpl(config: Config) extends Extension {
  private[this] val nest = config.getConfig("nest")
  val firebaseUrl = nest.getString("firebase.url")
}

object ServerSettings  extends ExtensionId[ServerSettingsImpl] with ExtensionIdProvider {
  def createExtension(system: ExtendedActorSystem) = new ServerSettingsImpl(system.settings.config)
  def lookup() = ServerSettings
}
