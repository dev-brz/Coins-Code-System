import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoinsViewComponent } from './coins-view.component';
import { CoinStore } from '../../../coins/store/coin.store';
import { signalStore, withMethods, withState } from '@ngrx/signals';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { CreateCoinViewComponent } from '../../../coins/components/create-coin-view/create-coin-view.component';
import { CoinBase } from '../../../coins/models/http/coin.model';
import { RouterTestingModule } from '@angular/router/testing';

describe('CoinsViewComponent', () => {
  let component: CoinsViewComponent;
  let fixture: ComponentFixture<CoinsViewComponent>;
  let dialog: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    const CoinStoreMock = signalStore(
      withState({ coinsEntities: [] }),
      withMethods(() => ({ replace: jasmine.createSpy() }))
    );
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, CoinsViewComponent],
      providers: [
        { provide: CoinStore, useClass: CoinStoreMock },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CoinsViewComponent);
    component = fixture.componentInstance;
    dialog = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open dialog on openCreateDialog call', () => {
    dialog.open.and.returnValue({ afterClosed: () => of({}) } as MatDialogRef<CreateCoinViewComponent>);
    component.openCreateDialog();
    expect(dialog.open).toHaveBeenCalled();
  });

  it('should unsubscribe on ngOnDestroy', () => {
    spyOn(component.subscriptions, 'unsubscribe');
    component.ngOnDestroy();
    expect(component.subscriptions.unsubscribe).toHaveBeenCalled();
  });

  it('should call coinStore.replace on drop', () => {
    const event = { previousIndex: 1, currentIndex: 2 } as CdkDragDrop<CoinBase[]>;
    component.drop(event);
    expect(component.coinStore.replace).toHaveBeenCalledWith(1, 2);
  });

  it('should navigate to create coin view when the screen size is mobile < 600px', () => {
    component.isDesktop$ = of(false);
    component.openCreateDialog = jasmine.createSpy();
    fixture.detectChanges();
    fixture.nativeElement.querySelector('a').click();
    expect(component.openCreateDialog).toHaveBeenCalledTimes(0);
  });

  it('should open dialog when it is desktop ', function () {
    component.isDesktop$ = of(true);
    component.openCreateDialog = jasmine.createSpy();

    fixture.nativeElement.querySelector('button').click();
    expect(component.openCreateDialog).toHaveBeenCalledTimes(1);
  });
});
