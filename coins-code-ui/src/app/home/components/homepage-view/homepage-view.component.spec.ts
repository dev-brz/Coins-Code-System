import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomepageViewComponent } from './homepage-view.component';

describe('HomepageComponent', () => {
  let component: HomepageViewComponent;
  let fixture: ComponentFixture<HomepageViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomepageViewComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(HomepageViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
