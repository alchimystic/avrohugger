package avrohugger
package format
package configurable

import avrohugger.stores.ClassStore
import org.apache.avro.Schema

import treehugger.forest._
import treehuggerDSL._

import scala.jdk.CollectionConverters._


object ExtraTraitTree {


  def toScalaEnumeratumDef(
                            classStore: ClassStore,
                            schema: Schema,
                            maybeBaseTrait: Option[String],
                            maybeFlags: Option[List[Long]]
                          ): List[Tree] = {

    val name = schema.getName

    val namespaceAnnot = Option(schema.getNamespace)
      .map(nm => ANNOT(s"""AvroNamespace("$nm")"""))

    val entryDef = TRAITDEF(name)
      .withFlags(Flags.SEALED)
      .withParents("EnumEntry")
      .withAnnots(namespaceAnnot.toList)

    val objectDef = OBJECTDEF(name)
      .withParents(s"Enum[$name]")
      .withParents(s"VulcanEnum[$name]")

    val valuesVal = VAL("values").withType(s"IndexedSeq[$name]") := REF("findValues")

    val symbolsList = schema.getEnumSymbols.asScala.map { s =>
      OBJECTDEF(s)
        .withFlags(Flags.CASE)
        .withParents(name)
    }

    val contents: List[Tree] = List(valuesVal) ++ symbolsList
    val objectTree = objectDef := BLOCK(contents)

    List(entryDef, objectTree)
  }
}
