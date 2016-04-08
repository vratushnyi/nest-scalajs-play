package components

import components.Bootstrap.MediaStyle
import domain.NestTypes._
import domain.Thermostat
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.ext.KeyCode

import scalacss.ScalaCssReact._

object ThermostatComponent {
  @inline private def bss = GlobalStyles.bootstrapStyles
  @inline private def bs = GlobalStyles

  case class Props(data: Thermostat, onTemperatureChange: (DeviceId, TemperatureC) => Callback)
  case class State(targetTemperature: String)

  class Backend($: BackendScope[Props, State]) {
    def renderTemperature(temp: Double) =
      "%1.1f".format(temp).split("\\.") match {
        case Array(a, "0") => <.div(a)
        case Array(a, b) => <.div(<.span(a), <.span(bs.decimal, b))
      }

    def targetTemperatureChanged: ReactEventI => Callback =
      e => {
        val text = e.target.value
        $.modState(s => s.copy(targetTemperature = text))
      }

    def onInputSubmit(p: Props)(e: ReactKeyboardEvent): Option[Callback] =
      e.nativeEvent.keyCode match {
        case KeyCode.Enter => Some(
          $.state.flatMap(s =>
            p.onTemperatureChange(p.data.device_id, adjustValue(s.targetTemperature))
          ))
        case _ => None
      }

    private def adjustValue(temp: String): TemperatureC =
      Math.round(temp.toDouble * 2) / 2.0d

    def render(p: Props, s: State) = {
      <.div(
        bss.media,
        <.div(
          bss.mediaOpt(MediaStyle.left),
          <.div(^.className := "numberCircle", renderTemperature(p.data.target_temperature_c))
        ),
        <.div(bss.mediaOpt(MediaStyle.body),
          <.div(bss.mediaOpt(MediaStyle.heading), <.h3(p.data.name)),
          <.p(
            "Target temperature: ",
            <.input.text(
              bs.temperatureInput,
              ^.value := s.targetTemperature,
              ^.onChange ==> targetTemperatureChanged,
              ^.onKeyDown ==>? onInputSubmit(p)
            ),
            <.span(" \u2103")
          )
        ))
    }
  }

  private val component = ReactComponentB[Props]("Nest Thermostats")
    .initialState_P(p => State(p.data.target_temperature_c.toString))
    .renderBackend[Backend]
    .componentWillReceiveProps { p =>
      p.$.setState(State(p.nextProps.data.target_temperature_c.toString))
    }.build

  def apply(data: Thermostat, updateTemperature: (DeviceId, TemperatureC) => Callback) = {
    component.withKey(data.device_id)(Props(data, updateTemperature))
  }
}
