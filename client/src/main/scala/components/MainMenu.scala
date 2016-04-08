package components

import app.Main.{DevicesLoc, Loc}
import components.Icon.Icon
import japgolly.scalajs.react.{ReactComponentB, ReactElement, ReactNode}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._

object MainMenu {
  case class Props(router: RouterCtl[Loc], currentLoc: Loc)

  private case class MenuItem(idx: Int,
                              label: (Props) => ReactNode,
                              icon: Option[Icon] = None,
                              location: Option[Loc] = None,
                              children: Seq[MenuItem] = Seq.empty)

  private val menuItems = Seq(
    MenuItem(idx = 1, label = _ => "Devices", location = Some(DevicesLoc))
  )

  private def buildTopMenuItem(props: Props, item: MenuItem): ReactElement =
    <.li(^.className := "dropdown",
      <.a(^.href := "#",
        ^.className := "dropdown-toggle",
        dataToggle := "dropdown",
        ^.role := "button",
        ariaHasPopup := "true",
        ariaExpanded := "false", buildLabel(props, item), " ", Icon.caretDown),
      <.ul(^.className := "dropdown-menu", item.children.map(buildMenuItem(props, _)))
    )

  private def buildMenuItem(props: Props, item: MenuItem): ReactElement =
    <.li(^.key := item.idx, item.location.exists(_.getClass == props.currentLoc.getClass) ?= (^.className := "active"),
      item.location.fold(buildLabel(props, item)) { location =>
        props.router.link(location)(buildLabel(props, item))
      })

  private def buildLabel(props: Props, item: MenuItem): ReactElement =
    <.span(item.icon.isDefined ?= item.icon.get, item.icon.isDefined ?= " ", item.label(props))

  private def buildMenu(props: Props, menu: Seq[MenuItem]): ReactElement = {
    <.ul(^.className := "nav navbar-nav",
      menu.map(m => if (m.children.nonEmpty) buildTopMenuItem(props, m) else buildMenuItem(props, m)))
  }

  val dataToggle = "data-toggle".reactAttr
  val dataTarget = "data-target".reactAttr
  val ariaExpanded = "aria-expanded".reactAttr
  val ariaHasPopup = "aria-haspopup".reactAttr

  private val component = ReactComponentB[Props]("MainMenu")
      .render_P(p => {
        <.nav(^.className := "navbar navbar-default",
          <.div(^.className := "container-fluid",
            <.div(^.className := "navbar-header",
              <.button(^.`type` := "button",
                ^.className :="navbar-toggle collapsed",
                dataToggle := "collapse",
                ariaExpanded := "false",
                <.span(^.className := "sr-only", "Toggle menu"),
                <.span(^.className := "icon-bar"),
                <.span(^.className := "icon-bar"),
                <.span(^.className := "icon-bar")
              ),
              <.a(^.className := "navbar-brand", ^.href := "https://nest.com/", "Nest")
            ),
            <.div(^.className := "collapse navbar-collapse", ^.id := "mainMenu-collapse-1", buildMenu(p, menuItems))
          )
        )
      }).build

  def apply(router: RouterCtl[Loc], currentLoc: Loc) = component(Props(router, currentLoc))
}
