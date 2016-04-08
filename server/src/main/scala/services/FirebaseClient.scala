package services

import akka.actor.ActorRef
import api.{NestDevicesMessage, ThermostatUpdated}
import com.firebase.client.{DataSnapshot, Firebase, FirebaseError, ValueEventListener}
import com.firebase.client.Firebase.{AuthListener, CompletionListener}
import com.google.gson.Gson
import domain.NestDataModel
import domain.NestTypes.{DeviceId, TemperatureC}
import upickle.default._

import scala.collection.JavaConversions._

class FirebaseClient(actor: ActorRef)(implicit cfg: ServerSettingsImpl) {
  private[this] val fb = new Firebase(cfg.firebaseUrl)
  val gson = new Gson()

  def auth(token: String): FirebaseClient = {
    fb.auth(token, new AuthListener {
      def onAuthError(e: FirebaseError) {
        actor ! e
      }

      def onAuthSuccess(a: AnyRef) {
        fb.addValueEventListener(new ValueEventListener {
          def onDataChange(snapshot: DataSnapshot) {
            val json = gson.toJson(snapshot.getValue)
            val model = read[NestDataModel](json)
            actor ! NestDevicesMessage(model.devices)
          }

          def onCancelled(err: FirebaseError) {
            actor ! err
          }
        })

      }

      def onAuthRevoked(e: FirebaseError) {
        actor ! e
      }
    })
    this
  }

  def updateThermostat(id: DeviceId, temperature: TemperatureC): Unit = {
    val m = Map[String, AnyRef]("target_temperature_c" -> temperature.asInstanceOf[AnyRef])

    fb.child("devices").child("thermostats").child(id).updateChildren(
      mapAsJavaMap(m), new CompletionListener {
        def onComplete(err: FirebaseError, firebase: Firebase) = {
          if (err == null) {
            actor ! ThermostatUpdated(id, temperature)
          }
        }
      }
    )
  }
}
