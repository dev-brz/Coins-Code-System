import { ChangeDetectionStrategy, Component, HostListener, OnInit, Optional, ViewChild } from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContainer,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious } from '@angular/material/stepper';
import { MatError, MatFormField, MatHint, MatLabel, MatSuffix } from '@angular/material/form-field';
import { FormBuilder, FormControl, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInput } from '@angular/material/input';
import { MatTextColumn } from '@angular/material/table';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { EMPTY, map, Observable, tap } from 'rxjs';
import { MatIcon } from '@angular/material/icon';
import { FileInput, FileValidator, MaterialFileInputModule } from 'ngx-custom-material-file-input';
import { getErrorMessage } from '../../../shared/utils/get-error-messages';
import { CoinComponent } from '../coin/coin.component';
import { Coin } from '../../models/coin.model';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';
import { MatLine } from '@angular/material/core';
import { CoinService } from '../../services/coin.service';
import { CustomValidators } from '../../../shared/validators';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'cc-create-coin-view',
  standalone: true,
  imports: [
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatDialogContent,
    MatDialogTitle,
    MatStep,
    MatFormField,
    MatStepper,
    ReactiveFormsModule,
    MatStepLabel,
    MatStepperPrevious,
    MatInput,
    MatStepperNext,
    MatLabel,
    MatTextColumn,
    MatHint,
    NgTemplateOutlet,
    AsyncPipe,
    MatError,
    MatIcon,
    MatSuffix,
    MaterialFileInputModule,
    CoinComponent,
    MatGridList,
    MatGridTile,
    MatDialogContainer,
    MatLine,
    RouterLink
  ],
  templateUrl: './create-coin-view.component.html',
  styleUrl: './create-coin-view.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateCoinViewComponent implements OnInit {
  readonly MAX_IMAGE_SIZE_BYTES = 2_097_152;
  private _fb: NonNullableFormBuilder = this._formBuilder.nonNullable;

  @ViewChild(MatStepper)
  stepper?: MatStepper;
  isDesktop$ = this.breakpointsObserver.observe(Breakpoints.XSmall).pipe(map(matcher => !matcher.matches));
  basicCoinForm = this._fb.group({
    name: ['', [Validators.required, Validators.minLength(3)], [CustomValidators.coinNameTaken(this.coinService)]],
    description: ['', Validators.maxLength(300)]
  });
  imageForm = this._fb.group({
    image: new FormControl(new FileInput(null), {
      nonNullable: false,
      validators: [FileValidator.maxContentSize(this.MAX_IMAGE_SIZE_BYTES)]
    })
  });

  topUpForm = this._fb.group({
    amount: [0.0, Validators.min(0.0)],
    description: ['', Validators.required]
  });

  getErrorMessage = getErrorMessage;

  profileImageValueChange$: Observable<File | null> = EMPTY;
  private chosenImageUrl: string | File = '/assets/coin.png';

  get coin(): Coin {
    return {
      uid: '',
      name: this.basicCoinForm.value.name,
      description: this.basicCoinForm.value.description,
      imageUrl: this.chosenImageUrl,
      amount: this.topUpForm.value.amount ?? 0.0
    };
  }

  get isCompleted(): boolean {
    return this.stepper?.selectedIndex == 2;
  }

  constructor(
    private _formBuilder: FormBuilder,
    private coinService: CoinService,
    private breakpointsObserver: BreakpointObserver,
    private router: Router,
    @Optional() private dialogRef?: MatDialogRef<CreateCoinViewComponent>
  ) {}

  ngOnInit(): void {
    this.stepper?.next();

    this.profileImageValueChange$ = this.imageForm.controls.image.valueChanges.pipe(
      map(value => value?.files[0] ?? null),
      tap(image => (this.chosenImageUrl = image ? URL.createObjectURL(image) : '/assets/coin.png'))
    );
  }
  @HostListener('window:keyup.esc') onEsc(): void {
    this.dialogRef?.close();
  }

  @HostListener('window:keyup.enter') onEnter(): void {
    if (this.isCompleted) {
      this.createCoin();
    } else {
      this.nextStep();
    }
  }

  nextStep(): void {
    this.stepper?.next();
  }

  createCoin(): void {
    if (this.dialogRef) {
      this.dialogRef?.close(this.coin);
    } else {
      this.router.navigateByUrl('/main/coins');
    }

    this.coinService.createCoinAndTopUp(
      this.basicCoinForm.getRawValue(),
      this.imageForm.getRawValue().image?.files[0] ?? null,
      { ...this.topUpForm.getRawValue(), coinUid: '', username: '' }
    );
  }
}
