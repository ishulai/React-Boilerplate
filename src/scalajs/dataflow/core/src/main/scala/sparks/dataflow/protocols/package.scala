package sparks.dataflow

import java.io.File
import java.net.URLClassLoader

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

package object protocols {

  class DynamicClassProxy(classPath: String, val className: String) {

    private val cl: URLClassLoader = null

    private lazy val cls =
      try {
        if (classPath.isEmpty)
          Class.forName(className)
        else {
          val cl = new URLClassLoader(
            Array(new File(classPath).toURI.toURL),
            this.getClass.getClassLoader
          )
          cl.loadClass(className)
        }
      }
      catch {
        case e: Exception =>
          close()
          throw e
      }

    private lazy val instance =
      try {
        val constructors = cls.getConstructors
        if (constructors.nonEmpty) {
          val constructor = constructors(0)
          constructor.newInstance(getParameters(constructor.getParameterTypes): _*)
        }
        else {
          throw new Exception("Class %s cannot be created dynamically.".format(className))
        }
      }
      catch {
        case e: Exception =>
          close()
          throw e
      }

    def invoke(methodName: String, paramTypes: Class[_]*)(args: Object*) = {
      val method = cls.getMethod(methodName, paramTypes: _*)
      method.invoke(instance, args: _*)
    }

    def close() = if (cl != null) cl.close()

    private def getParameters(types: Array[Class[_]]): Seq[Object] = {
      import scala.collection.mutable.ListBuffer
      val defaults = new ListBuffer[Any]
      types.foreach(t => defaults += (if (t.isPrimitive) t.## else null))
      defaults.map(_.asInstanceOf[Object])
    }

  }

  /**
    * SprayJSON format for Enumeration
    *
    */
  implicit def enumFormat[T <: Enumeration](implicit enu: T): RootJsonFormat[T#Value] =
    new RootJsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)
      def read(json: JsValue): T#Value = {
        json match {
          case JsString(txt) => enu.withName(txt)
          case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
        }
      }
    }

}