package overrides

import avrohugger.types._
import org.apache.avro.Schema
import treehugger.forest._
import treehuggerDSL._
import definitions._

import scala.jdk.CollectionConverters._

trait Overrides {

  def getDecimalType(decimalType: AvroScalaDecimalType, schema: Schema): Option[Type]

  def getEnumType: Option[AvroScalaEnumType]

  def getBigDecimalImport(schema: Schema): Option[String]

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

  override def getEnumType: Option[AvroScalaEnumType] = Some(ScalaEnumeratum)

  override def getBigDecimalImport(schema: Schema): Option[String] = {
    schema.getFields.asScala
      .flatMap(e => Option(e.schema().getLogicalType))
      .exists(_.getName == "decimal") match {
      case true => Some(importFor(cfg.bigDecimalsModule))
      case false => None
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

  override def getBigDecimalImport(schema: Schema): Option[String] = None

}
