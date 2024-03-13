import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { UserStore } from './user/store/user.store';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [{ provide: UserStore, useValue: jasmine.createSpyObj(['']) }]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
