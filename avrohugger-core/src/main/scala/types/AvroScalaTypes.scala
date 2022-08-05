package avrohugger
package types

import overrides.Overrides

case class AvroScalaTypes(
                           // primitive
                           int:      AvroScalaNumberType   = ScalaInt,
                           long:     AvroScalaNumberType   = ScalaLong,
                           float:    AvroScalaNumberType   = ScalaFloat,
                           double:   AvroScalaNumberType   = ScalaDouble,
                           boolean:  AvroScalaBooleanType  = ScalaBoolean,
                           string:   AvroScalaStringType   = ScalaString,
                           `null`:   AvroScalaNullType     = ScalaNull,
                           bytes:    AvroScalaBytesType    = ScalaByteArray,
                           // complex
                           fixed:    AvroScalaFixedType    = ScalaCaseClassWrapper,
                           record:   AvroScalaRecordType   = ScalaCaseClass,
                           `enum`:     AvroScalaEnumType   = Overrides.instance.getEnumType.getOrElse(ScalaEnumeration),
                           union:    AvroScalaUnionType    = OptionEitherShapelessCoproduct,
                           array:    AvroScalaArrayType    = ScalaSeq,
                           map:      AvroScalaMapType      = ScalaMap,
                           protocol: AvroScalaProtocolType = NoTypeGenerated,
                           // logical
                           decimal:  AvroScalaDecimalType  = ScalaBigDecimal(None), //todo csilva
                           //decimal:  AvroScalaDecimalType  = ScalaBigDecimalWithPrecision(None),
                           date:     AvroScalaDateType     = JavaTimeLocalDate,
                           timestampMillis: AvroScalaTimestampMillisType = JavaTimeInstant,
                           uuid:     AvroUuidType          = JavaUuid
)

object AvroScalaTypes {
  def defaults = new AvroScalaTypes()
}