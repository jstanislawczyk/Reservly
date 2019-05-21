package validation.direct_chat_message

import model.DirectChatMessage
import validation.Validator

object DirectChatMessageValidator extends Validator[DirectChatMessage] {

  private val minimumMessageSize = DirectChatMessageValidatorValues.minimumMessageSize
  private val maximumMessageSize = DirectChatMessageValidatorValues.maximumMessageSize

  override def validate(chatMessage: DirectChatMessage): Boolean = {
    var isValidate = true

    if(isValueNotInRange(chatMessage.message.length, minimumMessageSize, maximumMessageSize)) {
      isValidate = false
    }

    isValidate
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max
}
