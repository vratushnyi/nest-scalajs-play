package components

import domain.Thermostat
import domain.NestTypes.{DeviceId, TemperatureC}
import japgolly.scalajs.react.{Callback, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.language.existentials

object ThermostatsComponent {
  case class Props(data: Map[DeviceId, Thermostat], updateTemperature: (DeviceId, TemperatureC) => Callback)

  private val component = ReactComponentB[Props]("Nest Thermostats")
      .render_P(p =>
        <.div(
          p.data.map { case (id, v) => ThermostatComponent(v, p.updateTemperature) }
        )
      ).build

  def apply(data: Map[DeviceId, Thermostat], updateTemperature: (DeviceId, TemperatureC) => Callback) = {
    component(Props(data, updateTemperature))
  }
}
