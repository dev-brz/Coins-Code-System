import { Component, OnInit, input } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { getErrorMessage } from '../../../../shared/utils/get-error-messages';
import { User } from '../../../models/user.model';
import { UsersService } from '../../../services/users.service';
import { CustomValidators, ValidatorPatterns } from '../../../../shared/validators';

interface UpdateForm {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

@Component({
  selector: 'cc-update-user-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatInputModule, MatFormFieldModule, MatIconModule],
  templateUrl: './update-user-form.component.html',
  styleUrl: './update-user-form.component.scss'
})
export class UpdateUserFormComponent implements OnInit {
  currentUser = input.required<User>();

  readonly form = this.fb.group({
    firstName: this.fb.control('', [Validators.required]),
    lastName: this.fb.control('', [Validators.required]),
    email: this.fb.control('', [Validators.required, Validators.email]),
    phoneNumber: this.fb.control('', [Validators.required, Validators.pattern(ValidatorPatterns.phoneNumber)])
  });

  readonly getErrorMessage = getErrorMessage;

  constructor(
    private fb: NonNullableFormBuilder,
    private usersService: UsersService
  ) {}

  ngOnInit(): void {
    this.form.patchValue(this.currentUser());
    const { email, phoneNumber } = this.form.controls;
    email.addAsyncValidators(CustomValidators.emailTaken(this.usersService, [email.value]));
    phoneNumber.addAsyncValidators(CustomValidators.phoneNumberTaken(this.usersService, [phoneNumber.value]));
  }

  get valid(): boolean {
    return this.form.valid;
  }

  get value(): UpdateForm {
    return this.form.getRawValue();
  }

  get disabled(): boolean {
    return this.form.disabled;
  }

  set disabled(disable: boolean) {
    disable ? this.form.disable() : this.form.enable();
  }
}
