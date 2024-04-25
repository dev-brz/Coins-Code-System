import { Component, inject, OnDestroy, Signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Coin } from '../../../coins/models/coin.model';
import {
  MatAnchor,
  MatButton,
  MatFabButton,
  MatIconAnchor,
  MatIconButton,
  MatMiniFabButton
} from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { MatToolbar, MatToolbarRow } from '@angular/material/toolbar';
import { MatDialog } from '@angular/material/dialog';
import { CreateCoinViewComponent } from '../../../coins/components/create-coin-view/create-coin-view.component';
import { CoinStore } from '../../../coins/store/coin.store';
import { map, Subscription } from 'rxjs';
import { CoinComponent } from '../../../coins/components/coin/coin.component';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { RouterLink } from '@angular/router';
import { CdkDrag, CdkDragDrop, CdkDragPlaceholder, CdkDropList } from '@angular/cdk/drag-drop';
import { MatChipListbox } from '@angular/material/chips';
import { CREATE_COIN_ROUTE } from '../../../shared/configs/routes.config';

@Component({
  selector: 'cc-coins-view',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatFabButton,
    MatToolbar,
    MatToolbarRow,
    MatIconButton,
    MatMiniFabButton,
    MatIconAnchor,
    MatAnchor,
    CoinComponent,
    RouterLink,
    CdkDropList,
    CdkDrag,
    CdkDragPlaceholder,
    MatChipListbox,
    MatButton
  ],
  templateUrl: './coins-view.component.html',
  styleUrl: './coins-view.component.scss'
})
export class CoinsViewComponent implements OnDestroy {
  readonly CREATE = CREATE_COIN_ROUTE;
  coinStore = inject(CoinStore);
  coins: Signal<Coin[]> = this.coinStore.coinsEntities;
  isDesktop$ = this.breakpointsObserver.observe(Breakpoints.XSmall).pipe(map(matcher => !matcher.matches));
  subscriptions: Subscription = new Subscription();

  constructor(
    public dialog: MatDialog,
    private breakpointsObserver: BreakpointObserver
  ) {}

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(CreateCoinViewComponent, {
      width: '680px',
      height: '500px',
      disableClose: true,
      autoFocus: true
    });

    this.subscriptions.add(dialogRef.afterClosed().subscribe());
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  drop($event: CdkDragDrop<Coin[]>): void {
    this.coinStore.replace($event.previousIndex, $event.currentIndex);
  }
}
