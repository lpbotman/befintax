import { Component, Inject, signal } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import {Asset, AssetTransaction, TransactionType} from '../../../../core/models/asset.model';
import {DEFAULT_CREATE_TRANSACTION} from '../../models/AssetTransactionForm.model';
import {WalletService} from '../../services/wallet.service';

export interface TransactionDialogData {
  transaction?: AssetTransaction;
  assetId: number;
  transactionType: TransactionType;
}

@Component({
  selector: 'app-transaction-dialog',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions
  ],
  providers: [provideNativeDateAdapter()], // Requis pour MatDatepicker
  templateUrl: './transaction-dialog.component.html',
  styleUrls: ['./transaction-dialog.component.scss']
})
export class TransactionDialogComponent {
  isEditMode: boolean;
  transactionModel = signal({... DEFAULT_CREATE_TRANSACTION});
  transactionDialogData: TransactionDialogData | undefined;

  constructor(
    public dialogRef: MatDialogRef<TransactionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransactionDialogData,
    private walletService: WalletService
  ) {
    this.transactionDialogData = data;
    this.isEditMode = !!data.transaction;

    if (this.isEditMode && data.transaction) {
      this.transactionModel.set({ ...data.transaction });
    }
  }

  onSave(): void {
    if(!this.transactionDialogData || !this.transactionDialogData.assetId){
      return;
    }

    this.transactionModel().type = this.transactionDialogData.transactionType;
    this.walletService.addTransaction(this.transactionDialogData.assetId, this.transactionModel());
    this.dialogRef.close(this.transactionModel());
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
