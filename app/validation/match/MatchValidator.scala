package validation.`match`

import model.Match
import validation.Validator

object MatchValidator extends Validator[Match] {

  override def validate(game: Match): Boolean = {
    var isValid = true

    if(game.startDate.after(game.endDate)) {
      isValid = false
    }

    isValid
  }
}
