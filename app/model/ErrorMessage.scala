package model

import com.google.gson.Gson
import play.api.libs.json.{Json, OFormat}

case class ErrorMessage(
  httpCode: String,
  message: String
)

object ErrorMessage {
  def createErrorMessageJson(errorMessage: ErrorMessage): String = {
    val gson = new Gson
    gson.toJson(errorMessage)
  }

  def parseErrorMessageJson(errorMessageJson: String): ErrorMessage = {
    val gson = new Gson
    gson.fromJson(errorMessageJson, classOf[ErrorMessage])
  }

  implicit val errorFormat: OFormat[ChatMessage] = Json.format[ChatMessage]
}
