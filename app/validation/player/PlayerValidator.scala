package validation.player

import model.Player
import validation.Validator

object PlayerValidator extends Validator[Player] {

  private val minimumNameSize = PlayerValidatorValues.minimumNameSize
  private val maximumNameSize = PlayerValidatorValues.maximumNameSize

  private val minimumUrlSize = PlayerValidatorValues.minimumUrlSize
  private val maximumUrlSize = PlayerValidatorValues.maximumUrlSize

  override def validate(player: Player): Boolean = {
    if(isNotValidId(player.id)) {
      return false
    }

    if(isValueNotInRange(player.displayName.length, minimumNameSize, maximumNameSize)) {
      return false
    }

    if(isValueNotInRange(player.photoUrl.length, minimumUrlSize, maximumUrlSize)) {
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
    val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
    emailRegex.r.unapplySeq(email).isEmpty
  }
}
