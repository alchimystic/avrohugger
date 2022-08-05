package avrohugger
package format

import avrohugger.format.abstractions.{JavaTreehugger, ScalaTreehugger, SourceFormat}
import avrohugger.format.configurable.Overrides
import avrohugger.format.standard.{StandardJavaTreehugger, StandardScalaTreehugger}
import avrohugger.types.{AvroScalaTypes, ScalaEnumeratum}

object Configurable extends Standard {

  val toolShortDescription: String = Standard.toolShortDescription ++ ", using a env-injected configuration to override some aspects of code generation"
  val javaTreehugger: JavaTreehugger = StandardJavaTreehugger
  val scalaTreehugger: ScalaTreehugger = StandardScalaTreehugger

  override val defaultTypes: AvroScalaTypes = Overrides.instance.usesEnumeratum match {
    case true => Standard.defaultTypes.copy(`enum` = ScalaEnumeratum)
    case false => Standard.defaultTypes
  }

}
