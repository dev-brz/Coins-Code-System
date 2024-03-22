import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteImageDialogComponent } from './delete-image-dialog.component';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';

describe('DeleteImageDialogComponent', () => {
  let component: DeleteImageDialogComponent;
  let fixture: ComponentFixture<DeleteImageDialogComponent>;
  let debugElement: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteImageDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteImageDialogComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain necessary dialog elements', () => {
    // GIVEN WHEN
    const title = debugElement.query(By.directive(MatDialogTitle));
    const content = debugElement.query(By.directive(MatDialogContent));
    const actions = debugElement.query(By.directive(MatDialogActions));
    const buttons = debugElement.queryAll(By.directive(MatDialogClose));

    // THEN
    expect(title).toBeTruthy();
    expect(content).toBeTruthy();
    expect(actions).toBeTruthy();
    expect(buttons).toHaveSize(2);
  });
});
