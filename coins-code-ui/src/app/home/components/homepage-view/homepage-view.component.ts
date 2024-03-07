import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { UserBase } from '../../../user/models/user.model';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'cc-homepage-view',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, RouterLink, NgOptimizedImage],
  styleUrl: './homepage-view.component.scss',
  templateUrl: './homepage-view.component.html'
})
export class HomepageViewComponent implements OnInit {
  user!: UserBase;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.user = this.route.snapshot.data['user'];
  }
}
