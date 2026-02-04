import {Component, effect, computed, signal, inject} from '@angular/core';
import {MatButton, MatFabButton, MatIconButton} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {CurrencyPipe, DatePipe, DecimalPipe} from '@angular/common';
import {MatSort, MatSortHeader, Sort} from '@angular/material/sort';
import {WalletService} from '../../services/wallet.service';
import {Asset} from '../../../../core/models/asset.model';
import {AddAssetDialogComponent} from '../add-asset-dialog/add-asset-dialog.component';
import {TransactionType} from '../../../../core/models/asset.model';
import {TransactionDialogComponent} from '../transaction-dialog/transaction-dialog.component';
import {AssetChartComponent} from '../../../common/asset-chart/asset-chart.component';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {of, switchMap, timer} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {MarketDataApiService} from '../../../../core/services/market-data-api.service';

@Component({
  selector: 'app-assets',
  imports: [
    DatePipe,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFabButton,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    MatHeaderCellDef,
    AssetChartComponent,
    DecimalPipe,
    CurrencyPipe,
    MatIconButton,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem
  ],
  templateUrl: './assets.component.html',
  styleUrl: './assets.component.scss',
})
export class AssetsComponent {

  columnsToDisplay = ['symbol', 'name', 'quantity', 'actualValue', 'type'];
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'action']
  walletSort = signal<Sort>({active: '', direction: ''});
  expandedAsset: Asset | null = null;
  protected readonly TransactionType = TransactionType;

  constructor(private dialog: MatDialog, private walletService: WalletService) {
    effect(() => {
      const assets: Asset[] = this.walletService.assets();
    });

    timer(0, 30000).pipe(takeUntilDestroyed()).subscribe(() => {
      this.walletService.refreshAllPrices();
    });
  }

  ngOnInit() {
    this.walletService.loadAssets();
  }

  assetsDatasource = computed(() => {
    const assets = this.walletService.assets();
    const prices = this.walletService.marketPrices();
    const { active, direction } = this.walletSort();

    const assetRows: AssetRow[] = assets.map(asset => {
      const priceKey = `${asset.symbol}:${asset.exchange}`;
      const currentPrice = prices[priceKey] || 0;

      const buyingValue = asset.transactions?.reduce(
        (acc, t) => acc + (t.type === TransactionType.BUY ? t.price : -t.price),
        0
      ) ?? 0;

      const quantity = asset.transactions?.reduce(
        (acc, t) => acc + (t.type === TransactionType.BUY ? t.quantity : -t.quantity),
        0
      ) ?? 0;

      const actualValue = (currentPrice != 0) ? quantity * currentPrice : buyingValue;

      return {
        name: asset.name,
        symbol: asset.symbol,
        quantity: quantity,
        totalTransactions: asset.transactions?.length ?? 0,
        buyingValue: buyingValue,
        actualValue: actualValue,
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
          return b.id - a.id;
        }

        return timeA - timeB;
      }
    );
  }

  getAssetColor(type: string): string {
    switch (type) {
      case 'STOCK': return '#449f64'; // Indigo (Bleu pro)
      case 'CRYPTO': return '#F59E0B'; // Amber (Orange Bitcoin)
      case 'ETF': return '#06B6D4';    // Cyan
      case 'OBLIGATION': return '#8B5CF6'; // Violet
      default: return '#64748B';
    }
  }

  protected deleteAsset(asset: Asset) {
    if(asset){
      const confirmed = window.confirm("Êtes-vous sûr de vouloir supprimer cet actif et les transactions associées ?");

      if (confirmed) {
        this.walletService.deleteAsset(asset.id);
      }
    }
  }

  protected deleteTransaction(asset: Asset, transactionId: number) {
    const confirmed = window.confirm("Êtes-vous sûr de vouloir supprimer cette transaction ?");

    if (confirmed) {
      this.walletService.deleteTransaction(asset.id, transactionId);
    }
  }
}

interface AssetRow {
  name: string;
  symbol?: string;
  quantity: number;
  totalTransactions: number;
  buyingValue: number;
  actualValue: number;
  type: string;
  object: any;
}
