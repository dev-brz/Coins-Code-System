import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { RouterLink } from '@angular/router';
import { ACCOUNT_ROUTE } from '../../../shared/configs/routes.config';
import { UserStore } from '../../../user/store/user.store';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'cc-home-view',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatIconModule, RouterLink],
  styleUrl: './home-view.component.scss',
  templateUrl: './home-view.component.html'
})
export class HomeViewComponent {
  readonly userAccountRoute = `../${ACCOUNT_ROUTE}`;

  currentUser = inject(UserStore).currentUser;
}
