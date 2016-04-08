package components

import components.Bootstrap.MediaStyle
import diode.ModelR
import domain.NestTypes._
import domain.Camera
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

import scalacss.ScalaCssReact._
import scala.language.existentials

object CameraComponent {
  @inline private def bs = GlobalStyles
  @inline private def bss = GlobalStyles.bootstrapStyles
  case class Props(model: ModelR[_, Map[DeviceId, Camera]])

  private val component = ReactComponentB[Props]("Nest Camera")
      .render_P(p => {
        <.div(p.model().map { case (id, device) =>
          <.div(^.key := id, bss.media,
            <.div(
              bss.mediaOpt(MediaStyle.left),
              <.img(
                bs.cameraImage,
                ^.src := "/assets/images/nestcam.png")
            ),
            <.div(
              bss.mediaOpt(MediaStyle.body),
              <.div(
                bss.mediaOpt(MediaStyle.heading),
                <.h3(device.name)),
                <.div(bs.cameraStatus(device.is_online))
            ))
        })
      }).build

  def apply(model: ModelR[_, Map[DeviceId, Camera]]) = component(Props(model))
}
