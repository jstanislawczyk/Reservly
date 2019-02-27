package serializer

import com.google.gson.Gson
import model.ErrorMessage

object ErrorMessageJsonSerializer {
  private val gson = new Gson

  def toJson(errorMessage: ErrorMessage): String = {
    gson.toJson(errorMessage)
  }

  def fromJson(errorMessageJson: String): ErrorMessage = {
    gson.fromJson(errorMessageJson, classOf[ErrorMessage])
  }
}
