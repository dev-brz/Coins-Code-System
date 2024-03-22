import { Component } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormField, MatInputModule } from '@angular/material/input';
import { getErrorMessage } from '../../../../shared/utils/get-error-messages';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatDialogModule, MatInputModule, MatFormField, ReactiveFormsModule],
  templateUrl: './delete-account-dialog.component.html',
  styleUrl: './delete-account-dialog.component.scss'
})
export class DeleteAccountDialogComponent {
  readonly form = this.fb.group({
    passwordConfirm: this.fb.control('', [Validators.required])
  });

  readonly getErrorMessage = getErrorMessage;

  constructor(private fb: NonNullableFormBuilder) {}
}
