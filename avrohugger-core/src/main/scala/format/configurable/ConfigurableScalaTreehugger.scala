package format.configurable

import avrohugger.format.configurable.ConfigurableImporter
import avrohugger.format.standard.{StandardImporter, StandardScalaTreehugger}
import avrohugger.format.standard.avrohuggers.{StandardProtocolhugger, StandardSchemahugger}

object ConfigurableScalaTreehugger extends StandardScalaTreehugger {
  val schemahugger = StandardSchemahugger
  val protocolhugger = StandardProtocolhugger
  val importer = ConfigurableImporter
}
