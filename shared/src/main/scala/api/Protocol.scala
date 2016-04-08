package api

import domain.NestTypes.{DeviceId, TemperatureC}
import domain.NestDevices

sealed trait Protocol

case class NestDevicesMessage(devices: NestDevices) extends Protocol
case class UpdateThermostat(id: DeviceId, targetTemperature: TemperatureC) extends Protocol
case class ThermostatUpdated(id: DeviceId, targetTemperature: TemperatureC) extends Protocol
