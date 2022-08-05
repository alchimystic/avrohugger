package overrides

import java.util.Properties
import scala.io.Source

case class OverridesConfig(bigDecimalsModule: String)

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
    def getProp(key: String): String = Option(props.getProperty(key)).get

    OverridesConfig(
      getProp("bigDecimalsModule")
    )
  }

}
