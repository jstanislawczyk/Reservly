package serializer

import com.google.gson.Gson

trait JsonSerializer[T] {

  protected val gson = new Gson

  def toJson(t: T): String = {
    gson.toJson(t)
  }

  def fromJson(json: String): T = {
    gson.fromJson(json, classOf[Class[T]])
  }
}
