export interface TaxTransactionDetailDto {
  date: string;
  assetName: string;
  quantity: number;
  sellPrice: number;
  fiscalCost: number;
  taxableGain: number;
}

export interface TaxGainCalculationDto {
  totalGrossGain: number;
  totalTaxableGain: number;
  exemptionApplied: number;
  finalTaxableBase: number;
  estimatedTax: number;
  transactions: TaxTransactionDetailDto[];
}
