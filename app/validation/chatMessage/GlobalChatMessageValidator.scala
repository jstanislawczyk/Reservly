package validation.chatMessage

import model.GlobalChatMessage
import validation.Validator

object GlobalChatMessageValidator extends Validator[GlobalChatMessage] {

  private val minimumMessageSize = GlobalChatMessageValidatorValues.minimumMessageSize
  private val maximumMessageSize = GlobalChatMessageValidatorValues.maximumMessageSize

  override def validate(chatMessage: GlobalChatMessage): Boolean = {
    var isValidate = true

    if(isValueNotInRange(chatMessage.message.length, minimumMessageSize, maximumMessageSize)) {
      isValidate = false
    }

    isValidate
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max
}
