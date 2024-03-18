import { BreakpointObserver, Breakpoints, LayoutModule } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { map } from 'rxjs';
import { ACCOUNT_ROUTE, COINS_ROUTE, HISTORY_ROUTE, HOME_ROUTE } from '../../../shared/configs/routes.config';
import { AuthService } from '../../../shared/services/auth.service';
import { User } from '../../../user/models/user.model';

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
export class MainViewComponent implements OnInit {
  readonly homeViewRoute = HOME_ROUTE;
  readonly coinsViewRoute = COINS_ROUTE;
  readonly historyViewRoute = HISTORY_ROUTE;
  readonly accountViewRoute = ACCOUNT_ROUTE;
  readonly isXSmall = this.breakpointObserver.observe(Breakpoints.XSmall).pipe(map(state => state.matches));
  user!: User;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this.user = this.route.snapshot.data['user'];
  }

  logout(): void {
    this.authService.logout();
  }
}
