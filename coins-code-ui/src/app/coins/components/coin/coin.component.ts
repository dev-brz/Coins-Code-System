import { Component, Input } from '@angular/core';
import { MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { Coin } from '../../models/coin.model';
import { DecimalPipe } from '@angular/common';
import { CdkDrag, CdkDragPlaceholder } from '@angular/cdk/drag-drop';

@Component({
  selector: 'cc-coin',
  standalone: true,
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardTitle,
    MatCardSubtitle,
    DecimalPipe,
    CdkDragPlaceholder,
    CdkDrag
  ],
  templateUrl: './coin.component.html',
  styleUrl: './coin.component.scss'
})
export class CoinComponent {
  @Input()
  coin: Coin = { uid: '' };
  @Input()
  isNotFirst?: boolean;

  editImage(): void {}
}
