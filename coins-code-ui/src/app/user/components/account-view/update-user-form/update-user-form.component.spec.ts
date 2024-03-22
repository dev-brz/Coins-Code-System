import { ComponentFixture, TestBed } from '@angular/core/testing';

import { input } from '@angular/core';
import { FormControlName } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { User } from '../../../models/user.model';
import { UpdateUserFormComponent } from './update-user-form.component';

describe('UpdateUserFormComponent', () => {
  let component: UpdateUserFormComponent;
  let fixture: ComponentFixture<UpdateUserFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateUserFormComponent, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateUserFormComponent);
    component = fixture.componentInstance;
    component.currentUser = input({ username: 'TestUsername' } as User);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render all form fields', () => {
    // GIVEN WHEN
    const formControls = Object.keys(component.form.controls);
    const templateControls = fixture.debugElement
      .queryAll(By.directive(FormControlName))
      .filter(Boolean)
      .map(element => element.attributes['formControlName']);

    // THEN
    expect(formControls).toEqual(jasmine.arrayWithExactContents(templateControls));
  });
});
