import { AsyncPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormControl, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { FileInput, FileValidator, MaterialFileInputModule } from 'ngx-custom-material-file-input';
import { EMPTY, Observable, map } from 'rxjs';

import { RouterLink } from '@angular/router';
import { LOGIN_ROUTE } from '../../../shared/configs/routes.config';
import { getErrorMessage } from '../../../shared/utils/get-error-messages';
import { UsersService } from '../../services/users.service';
import { UserStore } from '../../store/user.store';
import { CustomValidators, ValidatorPatterns, ValidatorUtils } from '../../validators';
import { FileService } from '../../../shared/services/file.service';

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

  readonly MAX_IMAGE_SIZE_BYTES = FileService.MAX_IMAGE_SIZE_BYTES;

  readonly repeatPasswordErrorStateMatcher: ErrorStateMatcher = new ValidatorUtils.PasswordRepeatErrorStateMatcher();

  readonly getErrorMessage = getErrorMessage;

  readonly getPasswordRepeatErrorMessage = ValidatorUtils.getPasswordRepeatErrorMessage;

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

  onSubmit(): void {
    const formValue = this.registerForm.getRawValue();
    const requestBody = { ...formValue, profileImage: this.extractFileFromFileInput(formValue.profileImage) };
    this.userStore.saveNew(requestBody);
  }

  private extractFileFromFileInput(fileInput: FileInput | null): File | null {
    return fileInput ? fileInput.files[0] : null;
  }
}
