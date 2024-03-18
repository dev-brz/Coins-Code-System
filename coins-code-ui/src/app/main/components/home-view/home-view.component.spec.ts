import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivatedRoute } from '@angular/router';
import { User } from '../../../user/models/user.model';
import { HomeViewComponent } from './home-view.component';

describe('HomeComponent', () => {
  let component: HomeViewComponent;
  let fixture: ComponentFixture<HomeViewComponent>;

  beforeEach(async () => {
    const userMock = { username: 'Username' } as User;
    const activatedRouteMock = { parent: { snapshot: { data: { user: { userMock } } } } };

    await TestBed.configureTestingModule({
      imports: [HomeViewComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
