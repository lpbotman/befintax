import {Component, OnInit, effect, computed, ViewChild, AfterViewInit, signal} from '@angular/core';
import {Asset, AssetTransaction, TransactionType} from '../../core/models/asset.model';
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
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from '@angular/material/table';
import {TransactionDialogComponent} from './components/transaction-dialog/transaction-dialog.component';
import {MatIcon} from '@angular/material/icon';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {MatList, MatListItem} from '@angular/material/list';
import { WalletService } from './services/wallet.service';
import {MatSort, MatSortHeader, Sort} from '@angular/material/sort';

@Component({
  selector: 'app-wallet',
  imports: [MatButton, MatColumnDef, MatCell, MatHeaderRow, MatRow, MatCellDef, MatHeaderCell,
    MatHeaderCellDef, MatTable, MatHeaderRowDef, MatRowDef, MatFabButton, MatIcon, DatePipe, MatListItem, MatList, CurrencyPipe, MatSort, MatSortHeader],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss',
})
export class WalletComponent implements OnInit {

  columnsToDisplay = ['symbol', 'name', 'totalTransactions', 'totalValue', 'type'];
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'action']
  walletSort = signal<Sort>({active: '', direction: ''});

  expandedAsset: Asset | null = null;

  constructor(private dialog: MatDialog, private walletService: WalletService) {
    effect(() => {
      const assets: Asset[] = this.walletService.assets();
    });
  }

  ngOnInit() {
    this.walletService.loadAssets();
  }

  assetsDatasource = computed(() => {
    const assets = this.walletService.assets();
    const { active, direction } = this.walletSort();

    const assetRows: AssetRow[] = assets.map(asset => {
      const totalValue = asset.transactions?.reduce(
        (acc, t) => acc + t.quantity * t.price,
        0
      ) ?? 0;

      return {
        name: asset.name,
        symbol: asset.symbol,
        totalTransactions: asset.transactions?.length ?? 0,
        totalValue,
        type: asset.type,
        object: asset
      };
    });

    if (!active || direction === '') {
      return assetRows;
    }

    return [...assetRows].sort((a, b) => {
      const valueA = a[active as keyof AssetRow];
      const valueB = b[active as keyof AssetRow];

      if (valueA == null || valueB == null) return 0;

      const result =
        typeof valueA === 'string'
          ? valueA.localeCompare(valueB)
          : valueA > valueB ? 1 : -1;

      return direction === 'asc' ? result : -result;
    });
  });


  isExpanded(asset: Asset) {
    return this.expandedAsset?.id === asset.id;
  }

  toggle(asset: Asset) {
    this.expandedAsset = this.isExpanded(asset) ? null : asset;
  }

  protected openAddAssetDialog() {
    const dialogRef = this.dialog.open(AddAssetDialogComponent, {
      width: '800px',
      maxWidth: '90vw'
    });
  }

  protected openAddTransactionDialog(asset: Asset, transactionType: TransactionType) {
    const dialogRef = this.dialog.open(TransactionDialogComponent, {
      width: '400px',
      data: {assetId: asset.id, transactionType: transactionType}
    });
  }

  getTransactions(assetName: string){
    const assets: Asset[] = this.walletService.assets();
    return assets.find(asset => asset.name === assetName)?.transactions?.sort(
      (a, b) =>
      {
        const timeA = new Date(b.date).getTime();
        const timeB = new Date(a.date).getTime();

        if (timeA === timeB) {
          return a.id - b.id;
        }

        return timeA - timeB;
      }
    );
  }

  get totalWalletValue() {
    const assets: Asset[] = this.walletService.assets();
    return assets.reduce((acc, asset) => {
      const totalValue = asset.transactions
        ? asset.transactions.reduce((acc, transaction) =>
          acc + (transaction.quantity * transaction.price) * (transaction.type === TransactionType.BUY ? 1 : -1), 0)
        :
        0;
      return acc + totalValue;
    }, 0);
    }


  protected readonly TransactionType = TransactionType;
}

interface AssetRow {
  name: string;
  symbol?: string;
  totalTransactions: number;
  totalValue: number;
  type: string;
  object: any;
}
