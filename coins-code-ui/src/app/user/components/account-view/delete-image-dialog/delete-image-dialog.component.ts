import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatDialogModule],
  templateUrl: './delete-image-dialog.component.html',
  styleUrl: './delete-image-dialog.component.scss'
})
export class DeleteImageDialogComponent {}
