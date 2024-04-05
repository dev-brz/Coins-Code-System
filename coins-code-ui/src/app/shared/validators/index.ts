import { AsyncValidatorFn } from '@angular/forms';
import { UsersService } from '../../user/services/users.service';
import {
  _getPasswordRepeatErrorMessage,
  _passwordRepeat,
  _PasswordRepeatErrorStateMatcher
} from './password-repeat.validator';
import { _fieldTaken } from './field-taken.validator';
import { CoinService } from '../../coins/services/coin.service';

export const CustomValidators = {
  usernameTaken: (s: UsersService, ignoredValues: string[] = []): AsyncValidatorFn =>
    _fieldTaken((f, v) => s.existsBy(f, v), 'username', ignoredValues),
  emailTaken: (s: UsersService, ignoredValues: string[] = []): AsyncValidatorFn =>
    _fieldTaken((f, v) => s.existsBy(f, v), 'email', ignoredValues),
  phoneNumberTaken: (s: UsersService, ignoredValues: string[] = []): AsyncValidatorFn =>
    _fieldTaken((f, v) => s.existsBy(f, v), 'phoneNumber', ignoredValues),
  passwordRepeat: _passwordRepeat,
  coinNameTaken: (s: CoinService): AsyncValidatorFn => _fieldTaken((_, value) => s.exists(value), 'name', [''])
};

export const ValidatorUtils = {
  PasswordRepeatErrorStateMatcher: _PasswordRepeatErrorStateMatcher,
  getPasswordRepeatErrorMessage: _getPasswordRepeatErrorMessage
};

export const ValidatorPatterns = {
  phoneNumber: /^[+]?[(]?[0-9]{3}[)]?[-\s.]?[0-9]{3}[-\s.]?[0-9]{4,6}$/im
};
