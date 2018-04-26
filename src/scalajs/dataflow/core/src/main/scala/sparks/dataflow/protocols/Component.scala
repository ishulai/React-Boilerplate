package sparks.dataflow.protocols

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.circe._
import io.circe.generic.semiauto._
import spray.json._

object Component {

  case class ComponentRegistry(categories: Seq[Category]) {
    def find(categoryId: String): Category = categories.find(c => c.id.equalsIgnoreCase(categoryId)).orNull

    def findComponent(categoryId: String, componentId: String): Component = {
      val cat = find(categoryId)
      if (cat != null) cat.find(componentId) else null
    }

    def all: Seq[Component] = {
      var comps = Seq[Component]()
      categories.foreach { c => comps = comps ++ c.components }
      comps
    }

    def getCategoryId(componentId: String): String = {
      categories.foreach {
        c => {
          val x = c.components.find(cc => cc.id.equalsIgnoreCase(componentId)).orNull
          if (x != null) return c.id
        }
      }
      ""
    }
  }

  case class Category(id: String, label: String, components: Seq[Component]) {
    def find(componentId: String): Component = components.find(c => c.id.equalsIgnoreCase(componentId)).orNull
  }

  case class Component(id: String, interfaceDesc: UISpec, jarDesc: JarSpec = JarSpec.empty)

  case class UISpec(
                     toolboxDesc: ToolboxSpec,
                     vertexDesc: VertexSpec = VertexSpec.empty,
                     isScript: Boolean = false,
                     jarDesc: JarSpec = JarSpec.empty,
                     script: ScriptSpec = ScriptSpec.empty
                   )

  case class ScriptSpec(scriptFile: String, className: String)

  object ScriptSpec {
    def empty = ScriptSpec("", "")
  }

  case class ToolboxSpec(label: String, icon: String)

  case class VertexSpec(inPorts: Seq[PortSpec], outPorts: Seq[PortSpec])

  object VertexSpec {
    def empty = VertexSpec(Seq.empty, Seq.empty)
  }

  case class PortSpec(name: String, icon: String, multiConnection: Boolean)

  case class JarSpec(classPath: String, packageName: String, className: String) {

    def qualifiedName = s"$packageName.$className"

  }

  object JarSpec {
    def empty = JarSpec("", "", "")
  }

  /**
    * JsonSupport for encapsulated case classes
    */
  trait JsonSupport {
    implicit val componentRegistryEncoder: Encoder[ComponentRegistry] = deriveEncoder
    implicit val categoryEncoder: Encoder[Category] = deriveEncoder
    implicit val componentEncoder: Encoder[Component] = deriveEncoder
    implicit val uiSpecEncoder: Encoder[UISpec] = deriveEncoder
    implicit val toolboxSpecEncoder: Encoder[ToolboxSpec] = deriveEncoder
    implicit val vertexSpecEncoder: Encoder[VertexSpec] = deriveEncoder
    implicit val portSpecEncoder: Encoder[PortSpec] = deriveEncoder
    implicit val jarSpecEncoder: Encoder[JarSpec] = deriveEncoder
    implicit val jarSpecDecoder: Decoder[JarSpec] = deriveDecoder
    implicit val scriptSpecEncoder: Encoder[ScriptSpec] = deriveEncoder
    implicit val scriptSpecDecoder: Decoder[ScriptSpec] = deriveDecoder
  }

  trait AkkaJsonSupport extends JsonSupport with SprayJsonSupport with DefaultJsonProtocol {
    implicit val scriptSpecFormat = jsonFormat2(ScriptSpec.apply)
    implicit val jarSpecFormat = jsonFormat3(JarSpec.apply)
    implicit val portSpecFormat = jsonFormat3(PortSpec)
    implicit val vertexSpecFormat = jsonFormat2(VertexSpec.apply)
    implicit val toolboxSpecFormat = jsonFormat2(ToolboxSpec)
    implicit val uiSpecFormat = jsonFormat5(UISpec)
    implicit val componentFormat = jsonFormat3(Component)
    implicit val categoryFormat = jsonFormat3(Category)
    implicit val componentRegistryFormat = jsonFormat1(ComponentRegistry)
  }

}