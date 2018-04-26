package sparks.dataflow

import scala.collection.mutable.ListBuffer

package object graph {

  implicit class CastListBuffer[T](l: ListBuffer[T]) {

    // implicit val for orNull
    implicit val emptyValueOfT: Null <:< T = null

    def castFind[A <: T](p: A => Boolean): Option[A] = {
      val r = l.find(x => p(x.asInstanceOf[A])).orNull
      if (r == null) None else Some(r.asInstanceOf[A])
    }

    def castForeach[A <: T](f: A => Unit) = l.foreach(x => f(x.asInstanceOf[A]))

    //def castMap[A](f: T => _) = l.map(x => f(x.asInstanceOf[A]))

  }

}
