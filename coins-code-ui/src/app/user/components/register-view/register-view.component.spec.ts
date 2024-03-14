import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormControlName } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { signalStore, withMethods } from '@ngrx/signals';
import { FileInput } from 'ngx-custom-material-file-input';
import { UsersService } from '../../services/users.service';
import { UserStore } from '../../store/user.store';
import { RegisterViewComponent } from './register-view.component';

const MockedStore = signalStore(withMethods(() => ({ saveNew: jasmine.createSpy() })));

describe('RegisterViewComponent', () => {
  let component: RegisterViewComponent;
  let fixture: ComponentFixture<RegisterViewComponent>;
  let userStoreMock: InstanceType<typeof MockedStore>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterViewComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [
        { provide: UsersService, useValue: jasmine.createSpy() },
        { provide: UserStore, useClass: MockedStore }
      ]
    }).compileComponents();

    userStoreMock = TestBed.inject(UserStore) as unknown as InstanceType<typeof MockedStore>;
    fixture = TestBed.createComponent(RegisterViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call proper method on submit', () => {
    // WHEN
    component.onSubmit();

    // THEN
    expect(userStoreMock.saveNew).toHaveBeenCalledTimes(1);
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
