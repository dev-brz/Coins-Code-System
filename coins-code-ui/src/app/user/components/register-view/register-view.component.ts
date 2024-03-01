import { Component, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { AbstractControl, FormControl, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ErrorStateMatcher } from '@angular/material/core';

import { FileInput, FileValidator, MaterialFileInputModule } from 'ngx-custom-material-file-input';
import { EMPTY, Observable, map } from 'rxjs';

import { CustomValidators, ValidatorPatterns } from '../../validators';
import { getErrorMessage } from '../../../shared/utils/get-error-messages';
import { UsersService } from '../../services/users.service';
import { Router, RouterModule } from '@angular/router';

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
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MaterialFileInputModule,
    AsyncPipe
  ],
  templateUrl: './register-view.component.html',
  styleUrl: './register-view.component.scss'
})
export class RegisterViewComponent implements OnInit {
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
    private usersService: UsersService,
    private router: Router
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
    this.usersService.save(requestBody).subscribe(() => this.router.navigateByUrl('/login'));
  }

  private extractFileFromFileInput(fileInput: FileInput | null): File | null {
    return fileInput ? fileInput.files[0] : null;
  }
}
