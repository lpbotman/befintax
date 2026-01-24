import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {TaxesApiService} from '../../../../core/services/taxes-api.service';
import {Subject, takeUntil} from 'rxjs';
import {TaxGainCalculationDto} from '../../dtos/TaxGainCalculation.dto';
import {
  MatCard,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {MatDivider} from '@angular/material/list';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';
import {MatIcon} from '@angular/material/icon';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef, MatTable
} from '@angular/material/table';

@Component({
  selector: 'app-capital-gains-tax',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatCardSubtitle,
    CurrencyPipe,
    MatDivider,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIcon,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef,
    MatCell,
    MatHeaderCell,
    MatColumnDef,
    MatCellDef,
    MatHeaderCellDef,
    MatTable,
    DatePipe
  ],
  templateUrl: './capital-gains-tax.component.html',
  styleUrl: './capital-gains-tax.component.scss',
})
export class CapitalGainsTaxComponent implements OnInit, OnDestroy{

  taxGainCalculation = signal<TaxGainCalculationDto | undefined>(undefined);
  private destroy$ = new Subject<void>();

  displayedColumns: string[] = ['date', 'asset', 'qty', 'cost', 'sell', 'gain'];

  constructor(private taxesService: TaxesApiService) {}

  ngOnInit(): void {
        this.taxesService.calculateTaxGain(2026).pipe(takeUntil(this.destroy$)).subscribe(taxGainCalculation => {
          this.taxGainCalculation.set(taxGainCalculation);
          console.log(taxGainCalculation)
        });
    }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
