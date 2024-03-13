import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { User } from '../../../user/models/user.model';
import { USER_ACCOUNT_ROTUE } from '../../../shared/configs/routes.config';

@Component({
  selector: 'cc-homepage-view',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, RouterLink],
  styleUrl: './homepage-view.component.scss',
  templateUrl: './homepage-view.component.html'
})
export class HomepageViewComponent implements OnInit {
  readonly userAccountRoute = `../${USER_ACCOUNT_ROTUE}`;

  user!: User;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.user = this.route.snapshot.data['user'];
  }
}
