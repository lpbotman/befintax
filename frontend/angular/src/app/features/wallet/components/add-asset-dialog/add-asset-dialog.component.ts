import {Component, computed, effect, inject, OnDestroy, OnInit, signal, ViewChild} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { AssetType} from '../../../../core/models/asset.model';
import {form, FormField, required} from '@angular/forms/signals';
import {MatError, MatFormField, MatHint, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatDialogContent,MatDialogRef} from '@angular/material/dialog';
import {DEFAULT_CREATE_ASSET} from '../../models/AssetForm.model';
import {DEFAULT_CREATE_TRANSACTION} from '../../models/AssetTransactionForm.model';
import {MatStep, MatStepLabel, MatStepper, MatStepperPrevious} from '@angular/material/stepper';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatChipListbox, MatChipOption} from '@angular/material/chips';
import {WalletService} from '../../services/wallet.service';
import {AssetCreateDto} from '../../dtos/asset-create.dto';
import {AssetTransactionCreateDto} from '../../dtos/asset-transaction-create.dto';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  of,
  Subject,
  switchMap,
  takeUntil
} from 'rxjs';
import {InstrumentApiService} from '../../../../core/services/instrument-api.service';
import {MatAutocomplete,MatAutocompleteSelectedEvent,MatAutocompleteTrigger,MatOption} from '@angular/material/autocomplete';
import {Instrument} from '../../../../core/models/Instrument.model';
import {MatIcon} from '@angular/material/icon';
import {AsyncPipe} from '@angular/common';
import {TranslatePipe} from '@ngx-translate/core';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {provideLuxonDateAdapter} from '@angular/material-luxon-adapter';
import {MY_DATE_FORMATS} from '../../../../app.config';
import {MarketDataApiService} from '../../../../core/services/market-data-api.service';
import {USD_EUR_CURRENCY_RATE_END_2025} from '../../../../core/utils/constants.utils';

@Component({
  selector: 'app-add-asset-dialog',
  standalone: true,
  imports: [FormsModule, FormField, MatFormField, MatLabel, MatInput, MatButton, MatDialogContent,
    MatStepper, MatStep, MatStepLabel, MatRadioGroup, MatRadioButton,
    MatChipListbox, MatChipOption, MatAutocomplete, MatAutocompleteTrigger, MatOption, MatIcon,
    AsyncPipe, ReactiveFormsModule, TranslatePipe, MatIconButton, MatError, MatStepperPrevious, MatHint,
    MatDatepickerToggle, MatDatepicker, MatDatepickerInput, MatSuffix],
  providers: [
    provideLuxonDateAdapter(MY_DATE_FORMATS)
  ],
  templateUrl: './add-asset-dialog.component.html',
  styleUrls: ['./add-asset-dialog.component.scss']
})
export class AddAssetDialogComponent implements OnInit, OnDestroy {
  marketDataService = inject(MarketDataApiService);
  walletService = inject(WalletService);
  instrumentApiService = inject(InstrumentApiService);

  private destroy$ = new Subject<void>();

  @ViewChild(MatStepper) stepper!: MatStepper;

  protected assetModel = signal({...DEFAULT_CREATE_ASSET});
  assetForm= form(this.assetModel, (asset) => {
    required(asset.symbol);
    required(asset.name);
    required(asset.type);
    required(asset.taxRatePercent);
    required(asset.taxCollectedByBroker);
  });

  private transactionModel = signal({...DEFAULT_CREATE_TRANSACTION});
  transactionForm= form(this.transactionModel);

  searchControl = new FormControl<string | Instrument>('', {
    validators: [(control) => {
      return typeof control.value === 'string' ? { assetNotSelected: true } : null;
    }]
  });

  filteredInstruments$!: Observable<Instrument[]>;

  constructor(public dialogRef: MatDialogRef<AddAssetDialogComponent>) {
    effect(() => {
      const date = this.transactionForm.date()?.value();
      const currency = this.assetForm.currency().value();

      if( !(currency === 'EUR' || currency === 'USD')) return;

      if (!date) return;

      if (date < new Date('2025-12-31')) {
        this.marketDataService
          .getPriceEnd2025(
            this.assetForm.symbol().value(),
            this.assetForm.exchange().value()
          )
          .pipe(takeUntil(this.destroy$))
          .subscribe(price => {
            const convertedPrice =
              currency === 'EUR'
                ? Math.round((price / USD_EUR_CURRENCY_RATE_END_2025 + Number.EPSILON) * 100) / 100
                : Math.round((price + Number.EPSILON) * 100) / 100;

            this.assetForm.priceEnd2025().setControlValue(convertedPrice);
            console.log(price);
          });
      }
    });
  }

  ngOnInit(): void {
    this.filteredInstruments$ = this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      map(value => typeof value === 'string' ? value : (value?.symbol || '')),
      filter((val): val is string => !!val && val.length > 1),
      switchMap(val => {
        const assetType = this.assetModel().type;

        return this.instrumentApiService.search(val, assetType).pipe(
          catchError((err) => {
            console.error('Erreur recherche:', err);
            return of([]);
          })
        );
      })
    );
  }

  /*******************************************
            STEP 1 : FIND AN ASSET
   *****************************************/

  displayFn(instrument: Instrument): string {
    return instrument && instrument.symbol ? instrument.symbol : '';
  }

  onAssetSelected(event: MatAutocompleteSelectedEvent) {
    const asset = event.option.value as Instrument;

    this.assetModel.update(current => ({
      ...current,
      symbol: asset.symbol,
      name: asset.name,
      currency: asset.currency,
      exchange: asset.exchange,
    }));

    console.log(this.assetModel())
    //this.searchControl.setValue('', { emitEvent: false });
  }

  get searchTextLength(): number {
    const value = this.searchControl.value;
    return typeof value === 'string' ? value.length : 0;
  }

  protected onAssetTypeChange() {
    this.searchControl.setValue('', { emitEvent: true });
    this.assetForm.symbol().value.set('');
  }

  resetAsset() {
    this.assetModel.update(current => ({
      ...current,
      symbol: '',
      name: ''
    }));

    this.searchControl.setValue('');

    this.searchControl.markAsUntouched();
    this.searchControl.setErrors({ assetNotSelected: true });
  }

  /*******************************************
          STEP 3 : SAVE A NEW ASSET
   *****************************************/

  createAsset() {
    const wallet = this.walletService.wallet();

    const assetFormValue = this.assetModel();
    const transactionFormValue = this.transactionModel();

    const transactionCreateDto: AssetTransactionCreateDto = {
      date: transactionFormValue.date,
      quantity: transactionFormValue.quantity,
      price: transactionFormValue.price,
      type: transactionFormValue.type,
      currency: transactionFormValue.currency
    }

    const assetCreateDto: AssetCreateDto = {
      walletId: wallet?.id,
      name: assetFormValue.name,
      type: assetFormValue.type,
      taxCollectedByBroker: assetFormValue.taxCollectedByBroker,
      stockTaxRate: assetFormValue.taxRatePercent,
      symbol: assetFormValue.symbol,
      priceEnd2025: assetFormValue.priceEnd2025,
      currency: assetFormValue.currency,
      exchange: assetFormValue.exchange,
      transactions: [transactionCreateDto]
    }
    console.log(assetCreateDto);

    this.walletService.addAsset(assetCreateDto);
  }

  protected readonly AssetType = AssetType;
  protected readonly Object = Object;

  showEnd2025Price = computed(() => {
    const dateField = this.transactionForm.date();
    if (!dateField?.value) return false;

    const selectedDate = dateField.value;
    const limit = new Date('2025-12-31');
    return selectedDate() < limit;
  });


  protected onSave() {
    if (this.stepper.selectedIndex === 0) {
      this.searchControl.markAsTouched();
      this.searchControl.updateValueAndValidity();
      this.assetForm.symbol().markAsTouched();
      this.assetForm.name().markAsTouched();
      this.assetForm.type().markAsTouched();

      const isAssetSelected = !!this.assetForm.symbol().value();

      if (isAssetSelected && this.assetForm.name().valid()) {
        this.stepper.next();
      }
    } else if(this.stepper.selectedIndex === 1){
      this.assetForm.taxRatePercent().markAsTouched();
      this.assetForm.taxCollectedByBroker().markAsTouched();
      console.log(this.assetForm.taxRatePercent().valid(), this.assetForm.taxCollectedByBroker().valid());
      if (this.assetForm.taxRatePercent().valid() && this.assetForm.taxCollectedByBroker().valid()) {
        this.stepper.next();
      }
    } else {
      this.transactionForm.date().markAsTouched();
      this.transactionForm.quantity().markAsTouched();
      this.transactionForm.price().markAsTouched();
      this.assetForm.priceEnd2025().markAsTouched();
      if (this.transactionForm.date().valid() && this.transactionForm.quantity().valid()
        && this.transactionForm.price().valid() && this.assetForm.priceEnd2025().valid()) {

        this.createAsset();
        this.dialogRef.close();
      }
    }
  }

  protected onClose() {
    this.dialogRef.close();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
