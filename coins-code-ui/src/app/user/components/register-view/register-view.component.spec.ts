import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterViewComponent } from './register-view.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { UsersService } from '../../services/users.service';
import { By } from '@angular/platform-browser';
import { FormControlName } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { FileInput } from 'ngx-custom-material-file-input';
import { EMPTY } from 'rxjs';

describe('RegisterViewComponent', () => {
  let component: RegisterViewComponent;
  let fixture: ComponentFixture<RegisterViewComponent>;
  let userServiceMock: jasmine.SpyObj<UsersService>;

  beforeEach(async () => {
    userServiceMock = jasmine.createSpyObj<UsersService>(['existsByUsername', 'save']);

    await TestBed.configureTestingModule({
      imports: [RegisterViewComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [{ provide: UsersService, useValue: userServiceMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call proper method on submit', () => {
    // GIVEN
    userServiceMock.save.and.returnValue(EMPTY);

    // WHEN
    component.onSubmit();

    // THEN
    expect(userServiceMock.save).toHaveBeenCalledTimes(1);
  });

  it('should render all form fields', () => {
    // GIVEN WHEN
    const formControls = Object.keys(component.registerForm.controls);
    const templateControls = fixture.debugElement
      .queryAll(By.directive(FormControlName))
      .filter(Boolean)
      .map(element => element.attributes['formControlName']);

    // THEN
    expect(formControls).toEqual(jasmine.arrayWithExactContents(templateControls));
  });

  it('should emit file when file is selected', done => {
    // GIVEN
    const fileControl = component.registerForm.controls.profileImage;
    const sampleFile = new FileInput([new File([], 'filename')]);

    // WHEN
    component.profileImageValueChange$.subscribe(value => {
      // THEN
      expect(value).toBeInstanceOf(File);
      done();
    });
    fileControl.patchValue(sampleFile);
  });

  it('should emit null when file is removed', done => {
    // GIVEN
    const fileControl = component.registerForm.controls.profileImage;
    fileControl.patchValue(new FileInput([new File([], 'filename')]));

    // WHEN
    component.profileImageValueChange$.subscribe(value => {
      // THEN
      expect(value).toBeNull();
      done();
    });
    fileControl.patchValue(null);
  });
});
