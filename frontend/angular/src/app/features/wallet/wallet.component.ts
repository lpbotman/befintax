import {Component, OnInit} from '@angular/core';
import {CurrencyPipe} from '@angular/common';
import { WalletService } from './services/wallet.service';
import {MatTab, MatTabContent, MatTabGroup} from '@angular/material/tabs';
import {AssetsComponent} from './components/assets/assets.component';
import {Asset, TransactionType} from '../../core/models/asset.model';
import {AssetsTransactionsTaxComponent} from './components/assets-transactions-tax/assets-transactions-tax.component';
import {CapitalGainsTaxComponent} from './components/capital-gains-tax/capital-gains-tax.component';

@Component({
  selector: 'app-wallet',
  imports: [CurrencyPipe, MatTabGroup, MatTab, AssetsComponent, MatTabContent, AssetsTransactionsTaxComponent, CapitalGainsTaxComponent],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss',
})
export class WalletComponent implements OnInit {

  constructor(private walletService: WalletService) {}

  ngOnInit() {}

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
}

