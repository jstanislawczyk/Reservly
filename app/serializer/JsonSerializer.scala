package serializer

trait JsonSerializer[Object] {
  def toJson(model: Object): String
  def fromJson(json: String): Object
}
