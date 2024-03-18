import { CommonModule } from '@angular/common';
import { Component, WritableSignal, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule, UntypedFormControl, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router, RouterModule } from '@angular/router';
import { MAIN_ROUTE, REGISTER_ROUTE } from '../../../shared/configs/routes.config';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
  selector: 'cc-login-view',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    RouterModule
  ],
  templateUrl: './login-view.component.html'
})
export class LoginViewComponent {
  readonly registerRoute = `/${REGISTER_ROUTE}`;

  loginFailed: WritableSignal<boolean> = signal(false);

  loginForm: FormGroup = new FormGroup({
    username: new UntypedFormControl('', {
      validators: [Validators.required]
    }),
    password: new UntypedFormControl('', {
      validators: [Validators.required]
    })
  });

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.getRawValue()).subscribe({
        next: () => this.router.navigateByUrl(`/${MAIN_ROUTE}`),
        error: () => this.loginFailed.set(true)
      });
    }
  }
}
