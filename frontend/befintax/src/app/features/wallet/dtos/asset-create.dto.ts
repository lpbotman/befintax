export interface AssetCreateDto {
  name: string;
  symbol: string;
  type: AssetType;
  taxCollectedByBroker: boolean;
  stockTaxRate: number;
}
