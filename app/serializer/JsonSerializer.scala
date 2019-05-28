package serializer

trait JsonSerializer[T] {

  def toJson(t: T): String

  def fromJson(json: String): T
}
