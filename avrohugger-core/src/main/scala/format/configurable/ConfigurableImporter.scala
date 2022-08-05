package avrohugger
package format
package configurable

import avrohugger.format.standard.StandardImporter
import avrohugger.format.vulcan.Vulcan
import org.apache.avro.Schema
import treehugger.forest._
import treehuggerDSL._
import definitions._

import scala.jdk.CollectionConverters._

object ConfigurableImporter extends StandardImporter {

  lazy val overrides = Overrides.instance
  lazy val bgModule = overrides.getBigDecimalsModule
  lazy val EnumEntry = RootClass.newClass("enumeratum.EnumEntry")
  lazy val VulcanEnum = RootClass.newClass("enumeratum.VulcanEnum")

  //lazy val enumeratum  = overrides.getEnumType.map(_ == ScalaEnumeratum).getOrElse(false)
  //lazy val vulcan = overrides.

  override def extraImports(topLevelSchemas: List[Schema]): List[Import] =
    getBigDecimalImports(topLevelSchemas) ++ getEnumeratumImports(topLevelSchemas)

  private def getBigDecimalImport(schema: Schema): Option[String] = bgModule.flatMap { mod =>
    Option(schema)
      .filter(_.getType == Schema.Type.RECORD)
      .flatMap(_ =>
        schema.getFields.asScala
          .flatMap(e => Option(e.schema().getLogicalType))
          .exists(_.getName == "decimal") match {
          case true => Some(moduleImport(mod))
          case false => None
        }
      )
  }

  private def moduleImport(module: String): String = s"$module._"

  private def getEnumImports(schema: Schema): List[String] = overrides.usesEnumeratum match {
    case false => Nil
    case true => Option(schema)
      .toList
      .filter(_.getType == Schema.Type.ENUM)
      .flatMap { _ =>
        val result = List(EnumEntry)

        val ns = Option(schema.getNamespace).map(_ => Vulcan.NamespaceClass)

        val vulc = overrides.usesVulcan match {
          case true => Some(VulcanEnum)
          case false => None
        }

        (result ++ ns ++ vulc).map(_.toString())
      }
  }

  private def getEnumeratumImports(schemas: List[Schema]): List[Import] = {
    val enumImports0 = schemas
      .flatMap(getEnumImports(_))
      .toSet

    enumImports0
      .map(s => IMPORT(s))
      .toList
  }

  private def getBigDecimalImports(schemas: List[Schema]): List[Import] =
    bgModule
      .map { _ => schemas
        .flatMap(getBigDecimalImport(_))
        .headOption
        .map(s => IMPORT(s))
        .toList
      }
      .getOrElse(Nil)
}
