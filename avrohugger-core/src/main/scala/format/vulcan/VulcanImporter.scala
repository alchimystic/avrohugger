package avrohugger
package format
package vulcan

import avrohugger.format.standard.StandardImporter
import org.apache.avro.Schema
import treehugger.forest._
import treehuggerDSL._

object VulcanImporter extends StandardImporter {

  override def extraImports(topLevelSchemas: List[Schema]): List[Import] =
    List(IMPORT(Vulcan.CodecClass), IMPORT(Vulcan.CodecAuxClass))

}
