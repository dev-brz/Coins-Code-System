import { BreakpointObserver, Breakpoints, LayoutModule } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { map } from 'rxjs';
import {
  ACCOUNT_ROUTE,
  ARTICLES_ROUTE,
  COINS_ROUTE,
  HISTORY_ROUTE,
  HOME_ROUTE
} from '../../../shared/configs/routes.config';
import { AuthService } from '../../../shared/services/auth.service';
import { UserStore } from '../../../user/store/user.store';

@Component({
  selector: 'cc-main-view',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatMenuModule,
    MatIconModule,
    LayoutModule,
    AsyncPipe
  ],
  templateUrl: './main-view.component.html',
  styleUrl: './main-view.component.scss'
})
export class MainViewComponent {
  readonly homeViewRoute = HOME_ROUTE;
  readonly coinsViewRoute = COINS_ROUTE;
  readonly historyViewRoute = HISTORY_ROUTE;
  readonly accountViewRoute = ACCOUNT_ROUTE;
  readonly articlesViewRoute = ARTICLES_ROUTE;
  readonly isXSmall = this.breakpointObserver.observe(Breakpoints.XSmall).pipe(map(state => state.matches));
  currentUser = inject(UserStore).currentUser;

  constructor(
    private authService: AuthService,
    private breakpointObserver: BreakpointObserver
  ) {}

  logout(): void {
    this.authService.logout();
  }
}
