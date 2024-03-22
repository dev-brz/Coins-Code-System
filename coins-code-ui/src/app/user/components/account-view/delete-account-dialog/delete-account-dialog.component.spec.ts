import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteAccountDialogComponent } from './delete-account-dialog.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('DeleteAccountDialogComponent', () => {
  let component: DeleteAccountDialogComponent;
  let fixture: ComponentFixture<DeleteAccountDialogComponent>;
  let debugElement: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteAccountDialogComponent, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteAccountDialogComponent);
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
