import {Component, OnInit} from '@angular/core';
import {CurrencyPipe} from '@angular/common';
import { WalletService } from './services/wallet.service';
import {MatTab, MatTabContent, MatTabGroup} from '@angular/material/tabs';
import {AssetsComponent} from './components/assets/assets.component/assets.component';
import {Asset, TransactionType} from '../../core/models/asset.model';

@Component({
  selector: 'app-wallet',
  imports: [ CurrencyPipe,  MatTabGroup, MatTab, AssetsComponent, MatTabContent],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss',
})
export class WalletComponent implements OnInit {

  constructor(private walletService: WalletService) {
  }

  ngOnInit() {
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
}

