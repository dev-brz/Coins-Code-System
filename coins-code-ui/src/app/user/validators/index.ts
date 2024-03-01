import { _passwordRepeat } from './password-repeat.validator';
import { _usernameTaken } from './username-taken.validator';

export const CustomValidators = {
  usernameTaken: _usernameTaken,
  passwordRepeat: _passwordRepeat
};

export const ValidatorPatterns = {
  phoneNumber: /^[+]?[(]?[0-9]{3}[)]?[-\s.]?[0-9]{3}[-\s.]?[0-9]{4,6}$/im
};
