package components

import scalacss.Defaults._
import scalacss.mutable
import Bootstrap.CommonStyle._


class BootstrapStyles(implicit r: mutable.Register) extends StyleSheet.Inline()(r) {
  import dsl._

  val csDomain = Domain.ofValues(default, primary, success, info, warning, danger)

  val contextDomain = Domain.ofValues(success, info, warning, danger)

  val mediaDomain = {
    import Bootstrap.MediaStyle._
    Domain.ofValues(left, body, heading)
  }

  def commonStyle[A](domain: Domain[A], base: String) = styleF(domain)(opt =>
    styleS(addClassNames(base, s"$base-$opt"))
  )

  def styleWrap(classNames: String*) = style(addClassNames(classNames: _*))

  val buttonOpt = commonStyle(csDomain, "btn")

  val button = buttonOpt(default)

  val panelOpt = commonStyle(csDomain, "panel")

  val panel = panelOpt(default)

  val labelOpt = commonStyle(csDomain, "label")

  val label = labelOpt(default)

  val alert = commonStyle(contextDomain, "alert")

  val panelHeading = styleWrap("panel-heading")

  val panelBody = styleWrap("panel-body")

  val media = styleWrap("media")

  val mediaOpt = commonStyle(mediaDomain, "media")
}
