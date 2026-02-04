import {Component, computed, OnInit} from '@angular/core';
import {CurrencyPipe} from '@angular/common';
import { WalletService } from './services/wallet.service';
import {MatTab, MatTabContent, MatTabGroup} from '@angular/material/tabs';
import {AssetsComponent} from './components/assets/assets.component';
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

  ngOnInit() {
    this.walletService.loadAWallet();
  }

  totalWalletValue = computed(() => {
    return this.walletService.calculateTotalValue();
  });
}

