import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ChangePasswordDialogComponent } from './change-password-dialog.component';
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('ChangePasswordDialogComponent', () => {
  let component: ChangePasswordDialogComponent;
  let fixture: ComponentFixture<ChangePasswordDialogComponent>;
  let debugElement: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePasswordDialogComponent, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(ChangePasswordDialogComponent);
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
