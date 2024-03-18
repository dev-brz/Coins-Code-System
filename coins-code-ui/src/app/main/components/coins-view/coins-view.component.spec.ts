import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoinsViewComponent } from './coins-view.component';

describe('CoinsViewComponent', () => {
  let component: CoinsViewComponent;
  let fixture: ComponentFixture<CoinsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoinsViewComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(CoinsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
