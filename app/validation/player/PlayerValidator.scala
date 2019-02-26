package validation.player

import model.Player
import validation.Validator

object PlayerValidator extends Validator[Player] {

  private val minimumMessageSize = PlayerValidatorValues.minimumNameSize
  private val maximumMessageSize = PlayerValidatorValues.maximumNameSize

  override def validate(player: Player): Boolean = {
    var isValidate = true

    if(isValueNotInRange(player.firstName.length, minimumMessageSize, maximumMessageSize)) {
      isValidate = false
    }

    if(isValueNotInRange(player.lastName.length, minimumMessageSize, maximumMessageSize)) {
      isValidate = false
    }

    isValidate
  }

  private def isValueNotInRange = (value: Int, min: Int, max: Int) => value < min || value > max
}
