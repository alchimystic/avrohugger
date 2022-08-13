package avrohugger
package format
package configurable

import java.util.Properties
import scala.io.Source

case class OverridesConfig(bigDecimalsModule: Option[String],
                           enumeratum: Boolean,
                           vulcan: Boolean)

object OverridesConfig {

  private val configPathProp = "overridesConfigPath"

  lazy val instance: Option[OverridesConfig] = {
//    println(System.getenv())
//    println(System.getProperties)
    Option(System.getProperty(configPathProp))
      .orElse(Option(System.getenv(configPathProp)))
      .map { path =>
        val props = new Properties()
        val reader = Source.fromFile(path).bufferedReader()
        props.load(reader)
        reader.close()
        create(props)
      }
  }

  private def create(props: Properties): OverridesConfig = {
    def opt(key: String) = Option(props.getProperty(key))
    def bool(key: String) = opt(key).map(_.toBoolean)

    OverridesConfig(
      opt("bigDecimalsModule"),
      bool("enumeratum").getOrElse(false),
      bool("vulcan").getOrElse(false),
    )
  }

}
