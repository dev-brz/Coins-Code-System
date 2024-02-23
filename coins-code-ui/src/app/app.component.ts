import { Component, Inject, LOCALE_ID, isDevMode } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'cc-app',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  constructor(@Inject(LOCALE_ID) locale: string) {
    if (isDevMode()) {
      console.info(`Using locale: ${locale}`);
    }
  }
}
