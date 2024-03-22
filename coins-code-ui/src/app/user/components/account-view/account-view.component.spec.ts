import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { signalStore, withMethods, withState } from '@ngrx/signals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../shared/services/auth.service';
import { FileService } from '../../../shared/services/file.service';
import { UsersService } from '../../services/users.service';
import { UserStore } from '../../store/user.store';
import { AccountViewComponent } from './account-view.component';
import { UpdateUserFormComponent } from './update-user-form/update-user-form.component';

const UserStoreMock = signalStore(
  withState({ currentUser: { username: 'TestUsername' } }),
  withMethods(() => ({
    uploadProfileImage: jasmine.createSpy(),
    removeProfileImage: jasmine.createSpy(),
    update: jasmine.createSpy()
  }))
);

describe('AccountViewComponent', () => {
  let component: AccountViewComponent;
  let fixture: ComponentFixture<AccountViewComponent>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let usersServiceMock: jasmine.SpyObj<UsersService>;
  let fileServiceMock: jasmine.SpyObj<FileService>;
  let dialogMock: jasmine.SpyObj<MatDialog>;
  let userStoreMock: InstanceType<typeof UserStoreMock>;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj<AuthService>(['login', 'logout']);
    usersServiceMock = jasmine.createSpyObj<UsersService>(['updatePassword', 'delete']);
    fileServiceMock = jasmine.createSpyObj<FileService>(['selectImage']);
    dialogMock = jasmine.createSpyObj<MatDialog>(['open']);

    await TestBed.configureTestingModule({
      imports: [AccountViewComponent, NoopAnimationsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: UsersService, useValue: usersServiceMock },
        { provide: FileService, useValue: fileServiceMock },
        { provide: MatDialog, useValue: dialogMock },
        { provide: UserStore, useClass: UserStoreMock }
      ]
    })
      .overrideModule(MatDialogModule, { remove: { providers: [MatDialog] } })
      .compileComponents();

    userStoreMock = TestBed.inject(UserStore) as unknown as InstanceType<typeof UserStoreMock>;
    fixture = TestBed.createComponent(AccountViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('upload image should call proper method', () => {
    // GIVEN
    const file = new File([], 'filename.txt');
    fileServiceMock.selectImage.and.returnValue(of(file));

    // WHEN
    component.uploadImage();

    // THEN
    expect(userStoreMock.uploadProfileImage).toHaveBeenCalled();
    expect(userStoreMock.uploadProfileImage).toHaveBeenCalledWith(file);
  });

  it('upload image should not upload on error', () => {
    // GIVEN
    fileServiceMock.selectImage.and.returnValue(throwError(() => {}));

    // WHEN
    component.uploadImage();

    // THEN
    expect(userStoreMock.uploadProfileImage).not.toHaveBeenCalled();
  });

  it('delete image should call proper method on confirm', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of(true) } as MatDialogRef<unknown, unknown>);

    // WHEN
    component.deleteImage();

    // THEN
    expect(userStoreMock.removeProfileImage).toHaveBeenCalled();
  });

  it('delete image should ignore on cancel', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of(void 0) } as MatDialogRef<unknown, unknown>);

    // WHEN
    component.deleteImage();

    // THEN
    expect(userStoreMock.removeProfileImage).not.toHaveBeenCalled();
  });

  it('update user details should call proper method', () => {
    // GIVEN
    const form = { value: { firstName: 'UsernameUpdate' } } as UpdateUserFormComponent;

    // WHEN
    component.updateUserDetails(form);

    // THEN
    expect(userStoreMock.update).toHaveBeenCalled();
    expect(userStoreMock.update).toHaveBeenCalledWith({
      firstName: 'UsernameUpdate',
      username: userStoreMock.currentUser().username
    });
  });

  it('change password should call proper method on confirm', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of({}) } as MatDialogRef<unknown, unknown>);

    // WHEN
    component.changePassword();

    // THEN
    expect(usersServiceMock.updatePassword).toHaveBeenCalled();
  });

  it('change password should ignore on cancel', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of(void 0) } as MatDialogRef<unknown, unknown>);

    // WHEN
    component.changePassword();

    // THEN
    expect(usersServiceMock.updatePassword).not.toHaveBeenCalled();
  });

  it('delete account should delete on confirm and successful auth', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of('password') } as MatDialogRef<unknown, unknown>);
    authServiceMock.login.and.returnValue(of(void 0));

    // WHEN
    component.deleteAccount();

    // THEN
    expect(usersServiceMock.delete).toHaveBeenCalled();
  });

  it('delete account should ignore on cancel', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of(void 0) } as MatDialogRef<unknown, unknown>);

    // WHEN
    component.deleteAccount();

    // THEN
    expect(authServiceMock.login).not.toHaveBeenCalled();
    expect(usersServiceMock.delete).not.toHaveBeenCalled();
  });

  it('delete account should not delete on auth error', () => {
    // GIVEN
    dialogMock.open.and.returnValue({ afterClosed: () => of('password') } as MatDialogRef<unknown, unknown>);
    authServiceMock.login.and.returnValue(throwError(() => {}));

    // WHEN
    component.deleteAccount();

    // THEN
    expect(authServiceMock.login).toHaveBeenCalled();
    expect(usersServiceMock.delete).not.toHaveBeenCalled();
  });
});
