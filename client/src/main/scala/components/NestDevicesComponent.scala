package components

import api.UpdateThermostat
import diode.react.ModelProxy
import domain.NestDevices
import domain.NestTypes.{DeviceId, TemperatureC}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react._
import services.AppCircuit

object NestDevicesComponent {
  case class Props(proxy: ModelProxy[NestDevices], updateTemperature: (DeviceId, TemperatureC) => Callback)
  case class State(current: NestDevices)

  class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State) =
      <.div(
        ThermostatsComponent(p.proxy.value.thermostats, p.updateTemperature),
        CameraComponent(AppCircuit.zoom(_.devices.cameras))
      )
  }

  private val component = ReactComponentB[Props]("Nest Devices")
    .initialState_P(p => State(p.proxy.value))
    .renderBackend[Backend]
    .build

  def apply(proxy: ModelProxy[NestDevices]) = component(Props(proxy, (id, temp) => proxy.dispatch(UpdateThermostat(id, temp))))
}
