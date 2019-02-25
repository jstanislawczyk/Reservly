package validation

import model.ChatMessage

object ChatMessageValidator extends Validator[ChatMessage] {

  private val minimumMessageSize = ChatMessageValidatorValues.minimumMessageSize
  private val maximumMessageSize = ChatMessageValidatorValues.maximumMessageSize

  override def validate(chatMessage: ChatMessage): Boolean = {
    var isValidate = true

    if(isValueNotInRange(chatMessage.message.length, minimumMessageSize, maximumMessageSize)) {
      isValidate = false
    }

    isValidate
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max
}
