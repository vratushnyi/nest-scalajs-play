package components

import scalacss.Defaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  val bootstrapStyles = new BootstrapStyles

  val littleCircle = mixin(
    borderRadius(50.%%),
    width(15.px),
    height(15.px)
  )

  val cameraOnline = style(
    littleCircle,
    backgroundColor(c"#78BF4E")
  )

  val cameraOffline = style(
    littleCircle,
    backgroundColor(c"#E94443")
  )

  def cameraStatus(isOnline: Boolean) = if (isOnline) cameraOnline else cameraOffline

  val cameraImage = style(
    marginTop(7.px),
    marginLeft(20.px),
    marginRight(20.px)
  )

  val decimal = style(
    fontSize(50.%%)
  )

  val temperatureInput = style(
    width(50.px),
    paddingRight(5.px),
    textAlign.right
  )
}
