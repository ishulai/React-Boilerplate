package sparks.webui.base

import io.circe._
import io.circe.parser._
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.util.{Failure, Success}

trait AjaxClient {

  //protected val url: String = dom.window.location.href
  protected val url: String = "http://localhost:8080/spark/"

  case class Path(segments: String*) {
    override def toString(): String = segments.mkString("/")
  }

  def query[A: Decoder](path: Path, data: InputData, onSuccess: (A) => Unit, onFailure: (Throwable) => Unit): Unit = {
    post(path, data).onComplete {
      case Success(xhr) =>
        val result = decode[A](xhr.responseText)
        if (result.isRight)
          onSuccess(result.right.get)
        else
          onFailure(result.left.get)
      case Failure(e) => onFailure(e)
    }
  }

  def queryXml(path: Path, data: InputData, onSuccess: (js.Any) => _, onFailure: (Throwable) => Unit): Unit = {
    post(path, data).onComplete {
      case Success(xhr) => onSuccess(js.Dynamic.global.global.parseXml(xhr.responseText))
      case Failure(e) => onFailure(e)
    }
  }

  def queryPlainText(path: Path, data: InputData, onSuccess: (String) => _, onFailure: (Throwable) => Unit): Unit = {
    post(path, data).onComplete {
      case Success(xhr) => onSuccess(xhr.responseText)
      case Failure(e) => onFailure(e)
    }
  }

  private def post(path: Path, data: InputData) = {
    val slash = if (url.endsWith("/")) "" else "/"
    val u = "%s%s%s".format(url, slash, path.toString())
    if (data == null)
      Ajax.post(u, timeout = 120000) // todo: Temp 120000
    else
      Ajax.post(
        u,
        data,
        headers = Map(("Content-Type", "application/json")),
        timeout = 120000
      )
  }
}