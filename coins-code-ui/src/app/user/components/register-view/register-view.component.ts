import { AsyncPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { AbstractControl, FormControl, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { FileInput, FileValidator, MaterialFileInputModule } from 'ngx-custom-material-file-input';
import { EMPTY, Observable, map } from 'rxjs';

import { RouterLink } from '@angular/router';
import { getErrorMessage } from '../../../shared/utils/get-error-messages';
import { UsersService } from '../../services/users.service';
import { UserStore } from '../../store/user.store';
import { CustomValidators, ValidatorPatterns } from '../../validators';
import { LOGIN_ROUTE } from '../../../shared/configs/routes.config';

class RepeatPasswordErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: AbstractControl<unknown, unknown> | null): boolean {
    return !!((control?.touched && control?.invalid) || control?.parent?.hasError('passwordRepeat'));
  }
}

@Component({
  selector: 'cc-register-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MaterialFileInputModule,
    AsyncPipe,
    RouterLink
  ],
  templateUrl: './register-view.component.html',
  styleUrl: './register-view.component.scss'
})
export class RegisterViewComponent implements OnInit {
  readonly loginRoute = `/${LOGIN_ROUTE}`;

  readonly userStore = inject(UserStore);

  readonly MAX_IMAGE_SIZE_BYTES = 1_048_576;

  readonly repeatPasswordErrorStateMatcher: ErrorStateMatcher = new RepeatPasswordErrorStateMatcher();

  readonly registerForm = this.fb.group(
    {
      username: this.fb.control(
        '',
        [Validators.required, Validators.minLength(3)],
        [CustomValidators.usernameTaken(this.usersService)]
      ),
      firstName: this.fb.control('', [Validators.required]),
      lastName: this.fb.control('', [Validators.required]),
      email: this.fb.control('', [Validators.required, Validators.email]),
      phoneNumber: this.fb.control('', [Validators.pattern(ValidatorPatterns.phoneNumber)]),
      profileImage: new FormControl(new FileInput(null), {
        nonNullable: false,
        validators: [FileValidator.maxContentSize(this.MAX_IMAGE_SIZE_BYTES)]
      }),
      password: this.fb.control('', [Validators.required, Validators.minLength(8)]),
      passwordRepeat: this.fb.control('', [Validators.required])
    },
    { validators: [CustomValidators.passwordRepeat('password', 'passwordRepeat')] }
  );

  profileImageValueChange$: Observable<File | null> = EMPTY;

  constructor(
    private fb: NonNullableFormBuilder,
    private usersService: UsersService
  ) {}

  ngOnInit(): void {
    this.profileImageValueChange$ = this.registerForm.controls.profileImage.valueChanges.pipe(
      map(value => this.extractFileFromFileInput(value))
    );
  }

  getErrorMessage(formControlName: string): string | null {
    return getErrorMessage(this.registerForm, formControlName);
  }

  getPasswordRepeatErrorMessage(): string | null {
    const hasOwnErrors = !!this.registerForm.controls.passwordRepeat.errors;
    return getErrorMessage(this.registerForm, hasOwnErrors ? 'passwordRepeat' : '');
  }

  onSubmit(): void {
    const formValue = this.registerForm.getRawValue();
    const requestBody = { ...formValue, profileImage: this.extractFileFromFileInput(formValue.profileImage) };
    this.userStore.saveNew(requestBody);
  }

  private extractFileFromFileInput(fileInput: FileInput | null): File | null {
    return fileInput ? fileInput.files[0] : null;
  }
}
