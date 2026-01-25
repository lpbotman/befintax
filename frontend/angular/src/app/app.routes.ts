import { Routes } from '@angular/router';
import { WalletComponent } from './features/wallet/wallet.component';
import {authGuard} from './core/guards/auth.guard';
import {HomeComponent} from './features/home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'wallet', component: WalletComponent, canActivate: [authGuard] }
];
