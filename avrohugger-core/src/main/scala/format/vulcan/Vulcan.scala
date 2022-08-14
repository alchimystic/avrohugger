package avrohugger
package format
package vulcan

import avrohugger.format.abstractions.{JavaTreehugger, ScalaTreehugger, SourceFormat}
import avrohugger.matchers.TypeMatcher
import avrohugger.models.CompilationUnit
import avrohugger.stores.{ClassStore, SchemaStore}
import avrohugger.types.AvroScalaTypes
import treehugger.forest._
import definitions._
import org.apache.avro.{Protocol, Schema}

object Vulcan extends Standard {

  lazy val CodecClass = RootClass.newClass("vulcan.Codec")
  lazy val CodecAuxClass = RootClass.newClass("vulcan.Codec.Aux")
  lazy val NamespaceClass = RootClass.newClass("vulcan.generic.AvroNamespace")

  override val toolName: String = "generate-vulcan"
  val toolShortDescription = "Generates Scala code of Vulcan Codecs for Avro schema ADTs"

  //val defaultTypes: AvroScalaTypes = ???
  val javaTreehugger = ???
  val scalaTreehugger = ???
}
