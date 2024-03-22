import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { signalStore, withState } from '@ngrx/signals';
import { AuthService } from '../../../shared/services/auth.service';
import { UserStore } from '../../../user/store/user.store';
import { MainViewComponent } from './main-view.component';

const UserStoreMock = signalStore(withState({ currentUser: { username: 'TestUsername' } }));

describe('MainViewComponent', () => {
  let component: MainViewComponent;
  let fixture: ComponentFixture<MainViewComponent>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj<AuthService>(['logout']);

    await TestBed.configureTestingModule({
      imports: [MainViewComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: UserStore, useClass: UserStoreMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MainViewComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should trigger menu opening on clicking avatar', async () => {
    // GIVEN
    const avatar = fixture.debugElement.query(By.css('.avatar'));
    const menu = await loader.getHarness(MatMenuHarness.with({}));

    // WHEN
    (avatar.nativeElement as HTMLImageElement).click();

    // THEN
    expect(await menu.isOpen()).toBeTrue();
  });

  it('should invoke proper method on logout', () => {
    // WHEN
    component.logout();

    // THEN
    expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
  });
});
