package services

import api.{NestDevicesMessage, ThermostatUpdated, UpdateThermostat}
import diode._
import diode.react.ReactConnector
import diode.ActionResult.NoChange
import domain.{NestDevices, Thermostat}
import domain.NestTypes.DeviceId

object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  protected def initialModel = AppModel(domain.NestDevices())

  protected def actionHandler = combineHandlers(
    new NestDevicesHandler(zoomRW(_.devices)((m, v) => m.copy(devices = v))),
    new NestThermostatsHandler(
      zoomRW(_.devices)((m, v) => m.copy(devices = v))
        .zoomRW(_.thermostats)((m, v) => m.copy(thermostats = v))
    )
  )
}

class NestDevicesHandler[M](modelRW: ModelRW[M, NestDevices]) extends ActionHandler(modelRW) {
  def handle = {
    case NestDevicesMessage(devices) =>
      updated(devices)
  }
}

class NestThermostatsHandler[M](modelRW: ModelRW[M, Map[DeviceId, Thermostat]]) extends ActionHandler(modelRW) {
  def handle = {
    case msg @ UpdateThermostat(id, temp) =>
      WsClient.send(msg)
      NoChange
    case ThermostatUpdated(id, temp) =>
      updated(value.updated(id, value(id).copy(target_temperature_c = temp)))
  }
}
