import {Component, OnInit, signal} from '@angular/core';
import {firstValueFrom} from 'rxjs';
import {TaxesApiService} from '../../../../core/services/taxes-api.service';
import {AssetTransactionTaxDto} from '../../dtos/asset-transaction-tax.dto';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';

@Component({
  selector: 'app-assets-transactions-tax',
  imports: [
    MatTable,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderCell,
    MatColumnDef,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef
  ],
  templateUrl: './assets-transactions-tax.component.html',
  styleUrl: './assets-transactions-tax.component.scss',
})
export class AssetsTransactionsTaxComponent implements OnInit{

  groups = signal<TaxRateGroup[]>([]);

  displayedColumns = ['date', 'price', 'taxAmount'];
  dataSource = new MatTableDataSource<TransactionRow>([]);

  constructor(private taxesService: TaxesApiService) {}

  ngOnInit(): void {
    const now = new Date();
    const beginDate = new Date(now.getFullYear(), now.getMonth(), 1);
    const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    this.loadTransactions(beginDate, endDate);
  }

  async loadTransactions(beginDate: Date, endDate: Date) {
    const transactions = await firstValueFrom(
      this.taxesService.calculateAssetsTransactionTax(beginDate, endDate)
    );

    this.groups.set(this.prepareGroups(transactions));
  }

  isTotal(row: TransactionRow) {
    return row.type === 'total';
  }

  prepareGroups(transactions: AssetTransactionTaxDto[]): TaxRateGroup[] {
    const groupsMap = new Map<number, AssetTransactionTaxDto[]>();

    // Grouper par taxRate
    for (const tx of transactions) {
      const list = groupsMap.get(tx.taxRate) || [];
      list.push(tx);
      groupsMap.set(tx.taxRate, list);
    }

    // Construire les groupes avec totaux
    const groups: TaxRateGroup[] = [];

    for (const [taxRate, txList] of groupsMap.entries()) {
      const sorted = txList.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

      const totalPrice = sorted.reduce((sum, t) => sum + t.price, 0);
      const totalTaxAmount = sorted.reduce((sum, t) => sum + t.taxAmount, 0);

      groups.push({
        taxRate,
        transactions: sorted,
        total: {
          count: sorted.length,
          totalPrice,
          totalTaxAmount
        }
      });
    }

    return groups.sort((a, b) => a.taxRate - b.taxRate);
  }

  prepareMatTableRows(transactions: AssetTransactionTaxDto[]): TransactionRow[] {
    const groupsMap = new Map<number, AssetTransactionTaxDto[]>();

    for (const tx of transactions) {
      const list = groupsMap.get(tx.taxRate) || [];
      list.push(tx);
      groupsMap.set(tx.taxRate, list);
    }

    const rows: TransactionRow[] = [];

    for (const [taxRate, txList] of groupsMap.entries()) {
      const sorted = txList.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

      // Ajouter les transactions
      sorted.forEach(tx => rows.push({
        type: 'transaction',
        date: tx.date,
        price: tx.price,
        taxAmount: tx.taxAmount,
        taxRate: tx.taxRate
      }));

      // Ajouter la ligne total
      const totalPrice = sorted.reduce((sum, t) => sum + t.price, 0);
      const totalTaxAmount = sorted.reduce((sum, t) => sum + t.taxAmount, 0);

      rows.push({
        type: 'total',
        taxRate,
        count: sorted.length,
        totalPrice,
        totalTaxAmount
      });
    }

    return rows.sort((a, b) => a.taxRate - b.taxRate);
  }

}

export interface TaxRateGroup {
  taxRate: number;
  transactions: AssetTransactionTaxDto[];
  total: {
    count: number;
    totalPrice: number;
    totalTaxAmount: number;
  };
}

export interface TransactionRow {
  type: 'transaction' | 'total';
  date?: string;
  price?: number;
  taxAmount?: number;
  taxRate: number;
  count?: number;
  totalPrice?: number;
  totalTaxAmount?: number;
}

