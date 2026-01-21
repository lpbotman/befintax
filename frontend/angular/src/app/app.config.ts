import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';

import {provideTranslateService} from '@ngx-translate/core';
import {provideTranslateHttpLoader} from '@ngx-translate/http-loader';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {provideLuxonDateAdapter} from '@angular/material-luxon-adapter';

export const YEARMONTH_FORMATS = {
  parse: {dateInput: 'MM/yyyy'},
  display: {dateInput: 'MM/yyyy', monthYearLabel: 'MMM yyyy', dateA11yLabel: 'DD', monthYearA11yLabel: 'MMMM yyyy'},
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    provideRouter(routes),
    provideLuxonDateAdapter(YEARMONTH_FORMATS),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: '/assets/i18n/',
        suffix: '.json'
      }),
      fallbackLang: 'fr',
      lang: 'fr'
    })
  ]
};



