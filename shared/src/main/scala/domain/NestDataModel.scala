package domain

import domain.NestTypes.{DeviceId, TemperatureC}

object NestTypes {
  type DeviceId = String
  type TemperatureC = Double
}

case class NestDataModel(devices: NestDevices = NestDevices())

case class NestDevices(thermostats: Map[DeviceId, Thermostat] = Map.empty,
                       cameras: Map[DeviceId, Camera] = Map.empty)

case class Thermostat(device_id: DeviceId,
                      name: String,
                      target_temperature_c: TemperatureC = 0.0d,
                      hvac_mode: String = "off")

case class Camera(device_id: DeviceId,
                  name: String,
                  is_online: Boolean)