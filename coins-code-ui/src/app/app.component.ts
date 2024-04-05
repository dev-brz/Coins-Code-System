import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UserStore } from './user/store/user.store';

@Component({
  selector: 'cc-app',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [RouterOutlet]
})
export class AppComponent implements OnInit {
  readonly userStore = inject(UserStore);

  ngOnInit(): void {
    this.userStore.bootstrapCurrent();
  }
}
