import {Component, NgZone, OnInit, signal} from '@angular/core';
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
import {CurrencyPipe, DatePipe, TitleCasePipe} from '@angular/common';
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {MatFormField, MatHint, MatInput, MatInputModule, MatLabel} from '@angular/material/input';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from '@angular/material/datepicker';
import {provideLuxonDateAdapter} from '@angular/material-luxon-adapter';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DateTime} from 'luxon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {YEARMONTH_FORMATS} from '../../../../app.config';

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
    MatRowDef,
    CurrencyPipe,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatCardContent,
    MatCard,
    MatCardTitle,
    MatIcon,
    MatIconModule,
    MatFormField,
    MatLabel,
    MatDatepickerInput,
    MatHint,
    MatDatepickerToggle,
    MatDatepicker,
    MatInput,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    FormsModule,
    DatePipe,
    TitleCasePipe,
    MatProgressSpinner,
  ],
  templateUrl: './assets-transactions-tax.component.html',
  styleUrl: './assets-transactions-tax.component.scss',
})
export class AssetsTransactionsTaxComponent implements OnInit{

  groups = signal<TaxRateGroup[]>([]);

  displayedColumns = ['date', 'price', 'taxAmount'];
  isLoadingTransactions = signal<boolean>(false);

  constructor(private taxesService: TaxesApiService, private ngZone: NgZone) {}

  ngOnInit(): void {
    const now = new Date();
    const beginDate = new Date(now.getFullYear(), now.getMonth(), 1);
    const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    this.loadTransactions(beginDate, endDate);
  }

  async loadTransactions(beginDate: Date, endDate: Date) {
    this.isLoadingTransactions.set(true);

    this.taxesService.calculateAssetsTransactionTax(beginDate, endDate)
      .subscribe(transactions => {
        this.groups.set(this.prepareGroups(transactions));
        this.isLoadingTransactions.set(false);
      });
  }

  readonly yearMonthSelected = new FormControl<DateTime>(DateTime.now());

  setMonthAndYear(normalizedMonthAndYear: DateTime, datepicker: MatDatepicker<DateTime>) {
    const ctrlValue = DateTime.fromObject({
      month: normalizedMonthAndYear.month,
      year: normalizedMonthAndYear.year,
    });
    this.yearMonthSelected.setValue(ctrlValue);
    datepicker.close();

    const month = this.yearMonthSelected.value?.toJSDate().getMonth();
    const year = this.yearMonthSelected.value?.toJSDate().getFullYear();


    if (year !== undefined && month !== undefined) {
      const beginDate = new Date(year, month, 1);
      const endDate = new Date(year, month + 1, 0);

      this.loadTransactions(beginDate, endDate);
    }
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

  get totalTaxAmount(): number {
    return this.groups().reduce((sum, group) => sum + group.total.totalTaxAmount, 0);
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

