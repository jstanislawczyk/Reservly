package serializer

import com.google.gson.Gson
import model.ResponseMessage

object ErrorMessageJsonSerializer {
  private val gson = new Gson

  def toJson(errorMessage: ResponseMessage): String = {
    gson.toJson(errorMessage)
  }

  def fromJson(errorMessageJson: String): ResponseMessage = {
    gson.fromJson(errorMessageJson, classOf[ResponseMessage])
  }
}
