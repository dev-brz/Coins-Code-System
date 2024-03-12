import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivatedRoute } from '@angular/router';
import { User } from '../../../user/models/user.model';
import { HomepageViewComponent } from './homepage-view.component';

describe('HomepageComponent', () => {
  let component: HomepageViewComponent;
  let fixture: ComponentFixture<HomepageViewComponent>;

  beforeEach(async () => {
    const userMock = { username: 'Username' } as User;
    const activatedRouteMock = { snapshot: { data: { user: { userMock } } } };

    await TestBed.configureTestingModule({
      imports: [HomepageViewComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(HomepageViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
