import {Component, OnInit, signal, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Asset, AssetTransaction, AssetType} from '../../../../core/models/asset.model';
import {form, FormField, required} from '@angular/forms/signals';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {DEFAULT_CREATE_ASSET} from '../../models/AssetForm.model';
import {DEFAULT_CREATE_TRANSACTION} from '../../models/AssetTransactionForm.model';
import {MatStep, MatStepLabel, MatStepper} from '@angular/material/stepper';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatChipListbox, MatChipOption} from '@angular/material/chips';

@Component({
  selector: 'app-add-asset-dialog',
  standalone: true,
  imports: [FormsModule, FormField, MatFormField, MatLabel, MatInput, MatButton, MatDialogContent,
    MatStepper, MatStep, MatStepLabel, MatRadioGroup, MatRadioButton,
    MatChipListbox, MatChipOption],
  templateUrl: './add-asset-dialog.component.html',
  styleUrls: ['./add-asset-dialog.component.scss']
})
export class AddAssetDialogComponent implements OnInit {

  @ViewChild(MatStepper) stepper!: MatStepper;

  private assetModel = signal({...DEFAULT_CREATE_ASSET});
  assetForm= form(this.assetModel, (asset) => {
    required(asset.name);
    required(asset.type);
    required(asset.taxRatePercent);
    required(asset.taxCollectedByBroker);
  });

  private transactionModel = signal({...DEFAULT_CREATE_TRANSACTION});
  transactionForm= form(this.transactionModel);

  constructor(public dialogRef: MatDialogRef<AddAssetDialogComponent>) {}

  ngOnInit(): void {
  }

  addValidationRules(){

  }


  createAsset() {
    const assetFormValue = this.assetModel();
    const transactionFormValue = this.transactionModel();

    const transaction: AssetTransaction = {
      id: crypto.randomUUID() as any, // ou backend
      ...transactionFormValue,
    };

    const asset: Asset = {
      id: crypto.randomUUID() as any,
      ...assetFormValue,
      transactions: [transaction],
      symbol: assetFormValue.symbol || undefined
    };
    return asset;
  }

/*
  onClose() {
    this.closeDialog.emit();
  }
  */
  protected readonly AssetType = AssetType;
  protected readonly Object = Object;

  get showEnd2025Price(): boolean {
    const dateField = this.transactionForm.date();
    if (!dateField?.value) return false;

    const selectedDate = dateField.value;
    const limit = new Date('2025-12-31');
    return selectedDate() < limit;
  }

  protected onSave() {
    if (this.stepper.selectedIndex === 0) {
      this.assetForm.name().markAsTouched();
      this.assetForm.type().markAsTouched();
      if (this.assetForm.name().valid()) {
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

        const asset = this.createAsset();
        this.dialogRef.close(asset);
      }

    }


  }
}
