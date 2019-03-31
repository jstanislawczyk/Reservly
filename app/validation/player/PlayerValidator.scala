package validation.player

import model.Player
import validation.Validator

object PlayerValidator extends Validator[Player] {

  private val minimumNameSize = PlayerValidatorValues.minimumNameSize
  private val maximumNameSize = PlayerValidatorValues.maximumNameSize

  override def validate(player: Player): Boolean = {
    if(isNotValidId(player.id)) {
      return false
    }

    if(isValueNotInRange(player.displayName.length, minimumNameSize, maximumNameSize)) {
      return false
    }

    if(isValueNotInRange(player.email.length, minimumNameSize, maximumNameSize)) {
      return false
    }

    if(isEmailNotValid(player.email)) {
      return false
    }

    true
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max

  private def isNotValidId(value: String): Boolean = {

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
