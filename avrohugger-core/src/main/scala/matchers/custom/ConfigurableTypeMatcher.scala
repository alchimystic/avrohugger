package avrohugger
package matchers
package custom

import avrohugger.matchers.custom.CustomUtils._
import avrohugger.stores.ClassStore
import avrohugger.types._
import org.apache.avro.Schema
import treehugger.forest._
import treehuggerDSL._
import definitions._

object ConfigurableTypeMatcher {

//  def getEnumeratumType(classStore: ClassStore, schema: Schema, useFullName: Boolean = false) = useFullName match {
//    case true   =>   RootClass.newClass(s"${schema.getNamespace()}.${classStore.generatedClasses(schema)}")
//    case false  => classStore.generatedClasses(schema)
//  }

  def checkCustomDecimalType(decimalType: AvroScalaDecimalType, schema: Schema) =
    getDecimalType(schema)
      .getOrElse(CustomTypeMatcher.checkCustomDecimalType(decimalType, schema))


  private def getDecimalType(schema: Schema): Option[Type] =
    LogicalType(schema.getLogicalType) match {
      case Some(Decimal(precision, scale)) => Some(typeFor(precision, scale))
      case _ => None
    }

  private def typeFor(precision: Int, scale: Int): Type = {
    val name = s"BigDecimal${precision}p${scale}s"
    RootClass.newClass(nme.createNameType(name))
  }

}
