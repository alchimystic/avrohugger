package overrides

import avrohugger.types._
import org.apache.avro.Schema
import treehugger.forest._
import treehuggerDSL._
import definitions._
import treehugger.forest

import scala.jdk.CollectionConverters._

trait Overrides {

  def getDecimalType(decimalType: AvroScalaDecimalType, schema: Schema): Option[Type]

  def getEnumType: Option[AvroScalaEnumType]

  def getExtraImports(schemas: List[Schema]): List[Import]

}

object Overrides {

  lazy val instance: Overrides = OverridesConfig.instance.map(new ConfigurableOverrides(_)).getOrElse(NoOverrides)

}

class ConfigurableOverrides(cfg: OverridesConfig) extends Overrides {

  System.err.println(s"Using $cfg")

  override def getDecimalType(decimalType: AvroScalaDecimalType, schema: Schema): Option[Type] =
    LogicalType(schema.getLogicalType) match {
      case Some(Decimal(precision, scale)) => Some(typeFor(precision, scale))
      case _ => None
    }

  override def getExtraImports(schemas: List[Schema]): List[Import] = {

    val bdImports = schemas
      .flatMap(getBigDecimalImport(_))
      .headOption
      .map(s => IMPORT(s))
      .toList

    val enumImports0 = schemas
      .flatMap(getEnumImports(_))
      .toSet

      val enumImports = enumImports0
      .map(s => IMPORT(s))
      .toList

      bdImports ++ enumImports
  }

  override def getEnumType: Option[AvroScalaEnumType] = Some(ScalaEnumeratum)

  private def getBigDecimalImport(schema: Schema): Option[String] = {
    Option(schema)
      .filter(_.getType == Schema.Type.RECORD)
      .flatMap(_ =>
        schema.getFields.asScala
          .flatMap(e => Option(e.schema().getLogicalType))
          .exists(_.getName == "decimal") match {
            case true => Some(importFor(cfg.bigDecimalsModule))
            case false => None
          }
      )
  }

  private def getEnumImports(schema: Schema): List[String] = {
    Option(schema)
      .toList
      .filter(_.getType == Schema.Type.ENUM)
      .flatMap { _ =>
        val ns = Option(schema.getNamespace).map(_ => "vulcan.generic.AvroNamespace")
        List("enumeratum.EnumEntry", "enumeratum.VulcanEnum") ++ ns.toList
      }
  }

  private def importFor(module: String): String = s"$module._"

  private def typeFor(precision: Int, scale: Int): Type = {
    val name = s"BigDecimal${precision}p${scale}s"
    RootClass.newClass(nme.createNameType(name))
  }

}

object NoOverrides extends Overrides {

  override def getDecimalType(decimalType: AvroScalaDecimalType, schema: Schema): Option[Type] = None

  override def getEnumType: Option[AvroScalaEnumType] = None

  override def getExtraImports(schemas: List[Schema]): List[Import] = Nil

}
