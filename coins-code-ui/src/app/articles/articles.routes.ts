import { Routes } from '@angular/router';
import { ArticlesViewComponent } from './components/articles-view/articles-view.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ArticlesViewComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
