import {Component, signal} from '@angular/core';
import {Asset, AssetTransaction} from '../../core/models/asset.model';
import { AddAssetDialogComponent } from './components/add-asset-dialog/add-asset-dialog.component';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import {TransactionDialogComponent} from './components/transaction-dialog/transaction-dialog.component';
import {MatIcon} from '@angular/material/icon';
import {DatePipe} from '@angular/common';
import {MatList, MatListItem} from '@angular/material/list';
import { WalletService } from './services/wallet.service';

@Component({
  selector: 'app-wallet',
  imports: [MatButton, MatColumnDef, MatCell, MatHeaderRow, MatRow, MatCellDef, MatHeaderCell,
    MatHeaderCellDef, MatTable, MatHeaderRowDef, MatRowDef, MatFabButton, MatIcon, DatePipe, MatListItem, MatList],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss',
})
export class WalletComponent {

  assets = signal<Asset[]>([]);

  assetsDatasource = signal<AssetRow[]>([]);

  columnsToDisplay = ['symbol', 'name', 'totalTransactions', 'totalValue', 'type'];
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'action'];
  expandedAsset: Asset | null = null;

  constructor(private dialog: MatDialog, private walletService: WalletService) {}

  isExpanded(asset: Asset) {
    return this.expandedAsset === asset;
  }

  toggle(asset: Asset) {
    this.expandedAsset = this.isExpanded(asset) ? null : asset;
  }

  protected openAddAssetDialog() {
    const dialogRef = this.dialog.open(AddAssetDialogComponent, {
      width: '800px',
      maxWidth: '90vw'
    });

    dialogRef.afterClosed().subscribe((result: Asset | undefined) => {
      if (result) {
        const assets: Asset[] = this.assets();
        this.assets.set([...(assets || []), result]);

        const totalValue = result.transactions
          ? result.transactions.reduce((acc, transaction) => acc + transaction.quantity * transaction.price, 0)
          : 0;

        const newAsset = {
          name: result.name,
          symbol: result.symbol,
          totalTransactions: result.transactions?.length ?? 0,
          totalValue: totalValue,
          type: result.type,
          object: result
        };

        this.assetsDatasource.update((old: AssetRow[]) => [...old, newAsset]);

      }
      console.log(this.assetsDatasource);
    });
  }

  protected openAddTransactionDialog(asset: Asset) {
    const dialogRef = this.dialog.open(TransactionDialogComponent, {
      width: '400px',
      data: {asset: asset}
    });

    dialogRef.afterClosed().subscribe((transaction?: AssetTransaction) => {
      if (!transaction) return;

      this.assets.update(list =>
        list.map(a =>
          a.id === asset.id
            ? { ...a, transactions: [...(a.transactions ?? []), transaction] }
            : a
        )
      );
    });

  }

  getTransactions(assetName: string){
    const assets: Asset[] = this.assets();
    return assets.find(asset => asset.name === assetName)?.transactions?.sort(
      (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
    );
  }

}

interface AssetRow {
  name: string;
  symbol?: string;
  totalTransactions: number;
  totalValue: number;
  type: string;
  object: any;
}
