package serializer

import com.google.gson.Gson
import model.ResponseMessage

object ErrorMessageJsonSerializer extends JsonSerializer[ResponseMessage] {

  private val gson = new Gson

  override def toJson(responseMessage: ResponseMessage): String = {
    gson.toJson(responseMessage)
  }

  override def fromJson(json: String): ResponseMessage = {
    gson.fromJson(json, classOf[ResponseMessage])
  }
}
