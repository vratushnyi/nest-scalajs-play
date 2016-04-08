package components

object Bootstrap {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  // Common Bootstrap contextual styles
  object CommonStyle extends Enumeration {
    val default, primary, success, info, warning, danger = Value
  }

  object MediaStyle extends Enumeration {
    val left, body, heading = Value
  }
}
