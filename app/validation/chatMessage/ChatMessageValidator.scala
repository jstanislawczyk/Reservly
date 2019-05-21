package validation.chatMessage

import model.GlobalChatMessage
import validation.Validator

object ChatMessageValidator extends Validator[GlobalChatMessage] {

  private val minimumMessageSize = ChatMessageValidatorValues.minimumMessageSize
  private val maximumMessageSize = ChatMessageValidatorValues.maximumMessageSize

  override def validate(chatMessage: GlobalChatMessage): Boolean = {
    var isValidate = true

    if(isValueNotInRange(chatMessage.message.length, minimumMessageSize, maximumMessageSize)) {
      isValidate = false
    }

    isValidate
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max
}
