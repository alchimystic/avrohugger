package avrohugger
package format
package configurable

import treehugger.forest._

trait Overrides {

  def isConfigured: Boolean

  def getBigDecimalsModule: Option[String]

  def usesEnumeratum: Boolean

  def usesVulcan: Boolean

}

object Overrides {

  lazy val instance: Overrides = OverridesConfig.instance.map(new ConfigurableOverrides(_)).getOrElse(NoOverrides)

}

class ConfigurableOverrides(cfg: OverridesConfig) extends Overrides {

  System.err.println(s"Using $cfg")

  override def isConfigured: Boolean = true

  override def getBigDecimalsModule: Option[String] = cfg.bigDecimalsModule

  override def usesVulcan: Boolean = cfg.vulcan

  override def usesEnumeratum: Boolean = cfg.enumeratum

}

object NoOverrides extends Overrides {

  override def isConfigured: Boolean = false

  override def getBigDecimalsModule: Option[String] = None

  override def usesEnumeratum: Boolean = false

  override def usesVulcan: Boolean = false

}
