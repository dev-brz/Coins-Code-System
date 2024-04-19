import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FileInput } from 'ngx-custom-material-file-input';
import { CoinService } from '../../services/coin.service';
import { CreateCoinViewComponent } from './create-coin-view.component';
import { of } from 'rxjs';

describe('CreateCoinViewComponent', () => {
  let component: CreateCoinViewComponent;
  let fixture: ComponentFixture<CreateCoinViewComponent>;

  let dialogRefSpyObj: MatDialogRef<CreateCoinViewComponent>;
  let coinServiceSpyObj: CoinService;

  beforeEach(async () => {
    dialogRefSpyObj = jasmine.createSpyObj('MatDialogRef', ['close']);
    coinServiceSpyObj = jasmine.createSpyObj(
      'CoinService',
      { createCoinAndTopUp: null, exists: of(false) },
      { selectedIndex: 0 }
    );
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, BrowserAnimationsModule, CreateCoinViewComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpyObj },
        { provide: CoinService, useValue: coinServiceSpyObj }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateCoinViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close the dialog when pressing Esc key', () => {
    const event = new KeyboardEvent('keyup', { key: 'Escape' });
    window.dispatchEvent(event);
    expect(dialogRefSpyObj.close).toHaveBeenCalled();
  });

  it('should go to the next step when calling nextStep()', () => {
    component.stepper = jasmine.createSpyObj<MatStepper>('MatStepper', ['next']);
    component.nextStep();
    expect(component.stepper.next).toHaveBeenCalled();
  });

  it('should create a coin when pressing Enter key and is on the last step', () => {
    spyOn(component, 'createCoin');
    component.stepper = jasmine.createSpyObj<MatStepper>('MatStepper', ['selectedIndex'], { selectedIndex: 2 });

    const event = new KeyboardEvent('keyup', { key: 'Enter' });
    window.dispatchEvent(event);
    expect(component.createCoin).toHaveBeenCalled();
  });

  it('should create a coin and call the coin service when calling createCoin()', () => {
    const basicCoinFormValue = { name: 'Ethirium', description: 'my description does not matter mate' };
    const imageFormValue = { image: new FileInput([new File([], 'coin.png')]) };
    const topUpFormValue = { amount: 0.0, description: 'my description does not matter mate' };

    component.basicCoinForm.setValue(basicCoinFormValue);
    component.imageForm.setValue(imageFormValue);
    component.topUpForm.setValue(topUpFormValue);

    component.createCoin();

    expect(dialogRefSpyObj.close).toHaveBeenCalledWith(component.coin);
    expect(coinServiceSpyObj.createCoinAndTopUp).toHaveBeenCalledWith(
      basicCoinFormValue,
      imageFormValue.image.files[0],
      {
        ...topUpFormValue,
        coinUid: '',
        username: ''
      }
    );
  });
});
