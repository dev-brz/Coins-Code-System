import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { User } from '../../../user/models/user.model';
import { ACCOUNT_ROUTE } from '../../../shared/configs/routes.config';

@Component({
  selector: 'cc-home-view',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, RouterLink],
  styleUrl: './home-view.component.scss',
  templateUrl: './home-view.component.html'
})
export class HomeViewComponent implements OnInit {
  readonly userAccountRoute = `../${ACCOUNT_ROUTE}`;

  user!: User;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.user = this.route.parent?.snapshot.data['user'];
  }
}
