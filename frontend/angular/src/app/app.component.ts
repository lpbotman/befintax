import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AuthService} from './core/services/auth.service';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {toSignal} from '@angular/core/rxjs-interop';
import {map} from 'rxjs';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatToolbar} from '@angular/material/toolbar';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {MatDivider, MatListItem, MatNavList} from '@angular/material/list';
import {UpperCasePipe} from '@angular/common';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatMenuItem, MatButton, MatIcon, MatMenu, MatMenuTrigger, MatIconButton,
    MatToolbar, MatSidenavContent, MatListItem, MatDivider, MatNavList, MatSidenav, MatSidenavContainer, UpperCasePipe, TranslatePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit{
  private translate = inject(TranslateService);
  currentLang = 'fr';

  private breakpointObserver = inject(BreakpointObserver);
  auth = inject(AuthService);

  // Signal Réactif : Vrai si on est sur mobile/tablette (< 960px)
  isHandset = toSignal(
    this.breakpointObserver.observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
      .pipe(map(result => result.matches)),
    { initialValue: false }
  );

  constructor(private router: Router, private route: ActivatedRoute) {
    this.switchLang(this.translate.getBrowserLang() ?? 'fr');
  }

  ngOnInit(): void {}

  // Configuration du Menu
  menuItems = [
    { labelKey: 'menu.home', link: '/', icon: 'home' },
    { labelKey: 'menu.wallet', link: '/wallet', icon: 'account_balance_wallet' },
    /*{ labelKey: 'menu.fire', link: '/fire-tools', icon: 'trending_up' }*/
  ];

  switchLang(lang: string) {
    const supportedLangs = ['fr', 'nl'];

    if (!supportedLangs.includes(lang)) {
      lang = 'fr';
    }

    this.translate.use(lang);
    this.currentLang = lang;
  }

}


