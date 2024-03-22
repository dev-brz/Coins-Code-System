import { ComponentFixture, TestBed } from '@angular/core/testing';

import { signalStore, withState } from '@ngrx/signals';
import { HomeViewComponent } from './home-view.component';
import { UserStore } from '../../../user/store/user.store';
import { RouterTestingModule } from '@angular/router/testing';

const UserStoreMock = signalStore(withState({ currentUser: { username: 'TestUsername' } }));

describe('HomeComponent', () => {
  let component: HomeViewComponent;
  let fixture: ComponentFixture<HomeViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeViewComponent, RouterTestingModule],
      providers: [{ provide: UserStore, useClass: UserStoreMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
