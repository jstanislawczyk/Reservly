package validation.player

import model.Player
import validation.Validator

object PlayerValidator extends Validator[Player] {

  private val minimumMessageSize = PlayerValidatorValues.minimumNameSize
  private val maximumMessageSize = PlayerValidatorValues.maximumNameSize

  override def validate(player: Player): Boolean = {
    if(isStringNullOrWhiteSpace(player.id)) {
      return false
    }

    if(isValueNotInRange(player.displayName.length, minimumMessageSize, maximumMessageSize)) {
      return false
    }

    if(isValueNotInRange(player.email.length, minimumMessageSize, maximumMessageSize)) {
      return false
    }

    if(isEmailNotValid(player.email)) {
      return false
    }

    true
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max

  private def isStringNullOrWhiteSpace(value: String): Boolean = {

    if (value == null) {
      return true
    }

    value.foreach(char => {
      if(Character.isWhitespace(char)) {
        return true
      }
    })

    false
  }

  private def isEmailNotValid(email: String): Boolean = {
    """(\w+)@([\w\.]+)""".r.unapplySeq(email).isEmpty
  }
}
