package avrohugger
package format
package standard
package trees

import avrohugger.stores.ClassStore
import generators.ScalaDocGenerator
import matchers.TypeMatcher
import types._
import treehugger.forest._
import definitions._
import treehuggerDSL._
import org.apache.avro.{Protocol, Schema}
import org.apache.avro.Schema.Type.{ENUM, RECORD}

import scala.jdk.CollectionConverters._

object StandardTraitTree {

  def toADTRootDef(protocol: Protocol, typeMatcher: TypeMatcher) = {
    def isEnum(schema: Schema) = schema.getType == ENUM
    val sealedTraitTree = TRAITDEF(protocol.getName).withFlags(Flags.SEALED)
    val adtRootTree = {
      val adtSubTypes = typeMatcher.avroScalaTypes.enum match {
        case JavaEnum => protocol.getTypes().asScala.toList.filterNot(isEnum)
        case ScalaCaseObjectEnum => protocol.getTypes().asScala.toList
        case ScalaEnumeration => protocol.getTypes().asScala.toList
        case ScalaEnumeratum => protocol.getTypes().asScala.toList
        case EnumAsScalaString => protocol.getTypes().asScala.filterNot(isEnum)
      }
      if (adtSubTypes.forall(schema => schema.getType == RECORD)) {
        sealedTraitTree
          .withParents("Product")
          .withParents("Serializable")
      }
      else sealedTraitTree
    } 
    val treeWithScalaDoc = ScalaDocGenerator.docToScalaDoc(
      Right(protocol),
      adtRootTree)
      
    treeWithScalaDoc
  }
  
  def toCaseObjectEnumDef(schema: Schema,
    maybeBaseTrait: Option[String]): List[Tree] = {
    val adtRootTree: Tree = maybeBaseTrait match {
      case Some(baseTrait) =>
        TRAITDEF(schema.getName).withFlags(Flags.SEALED).withParents(baseTrait)
      case None =>
        TRAITDEF(schema.getName).withFlags(Flags.SEALED)
    }
    val adtSubTypes: List[Tree] = schema.getEnumSymbols.asScala
      .map(enumSymbol => enumSymbol.toString)
      .map(enumSymbolString => {
        (CASEOBJECTDEF(enumSymbolString).withParents(schema.getName): Tree)
      }).toList
    val objectTree = OBJECTDEF(schema.getName) := Block(adtSubTypes:_*)
    val adtRootTreeWithScalaDoc: Tree = ScalaDocGenerator.docToScalaDoc(
      Left(schema),
      adtRootTree)
    List(adtRootTreeWithScalaDoc, objectTree)
  }

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

    val objectDef =  OBJECTDEF(name)
      .withParents(s"Enum[$name]")
      .withParents(s"VulcanEnum[$name]")

    val valuesVal = VAL("values").withType(s"IndexedSeq[$name]") := REF("findValues")

    val symbolsList = schema.getEnumSymbols.asScala.map{ s =>
      OBJECTDEF(s)
        .withFlags(Flags.CASE)
        .withParents(name)
    }

    val contents: List[Tree] = List(valuesVal) ++ symbolsList
    val objectTree = objectDef := BLOCK(contents)

    List(entryDef, objectTree)
  }

}
