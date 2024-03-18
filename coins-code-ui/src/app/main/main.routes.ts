import { Routes } from '@angular/router';
import { ACCOUNT_ROUTE, COINS_ROUTE, HISTORY_ROUTE, HOME_ROUTE } from '../shared/configs/routes.config';
import { AccountViewComponent } from '../user/components/account-view/account-view.component';
import { HomeViewComponent } from './components/home-view/home-view.component';
import { CoinsViewComponent } from './components/coins-view/coins-view.component';
import { HistoryViewComponent } from './components/history-view/history-view.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: HOME_ROUTE,
    pathMatch: 'full'
  },
  {
    path: HOME_ROUTE,
    component: HomeViewComponent
  },
  {
    path: COINS_ROUTE,
    component: CoinsViewComponent
  },
  {
    path: HISTORY_ROUTE,
    component: HistoryViewComponent
  },
  {
    path: ACCOUNT_ROUTE,
    component: AccountViewComponent
  }
];
