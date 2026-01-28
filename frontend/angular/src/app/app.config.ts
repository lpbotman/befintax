import { ApplicationConfig } from '@angular/core';
import {HttpClient, provideHttpClient, withInterceptors} from '@angular/common/http';

import {provideTranslateLoader, provideTranslateService, TranslateLoader} from '@ngx-translate/core';
import {provideTranslateHttpLoader, TranslateHttpLoader} from '@ngx-translate/http-loader';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {provideLuxonDateAdapter} from '@angular/material-luxon-adapter';
import {
  AutoRefreshTokenService, createInterceptorCondition, INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  includeBearerTokenInterceptor, provideKeycloak, UserActivityService, withAutoRefreshToken
} from 'keycloak-angular';
import {environment} from '../environments/environment';
import {provideCharts, withDefaultRegisterables} from 'ng2-charts';

export const YEARMONTH_FORMATS = {
  parse: {dateInput: 'MM/yyyy'},
  display: {dateInput: 'MM/yyyy', monthYearLabel: 'MMM yyyy', dateA11yLabel: 'DD', monthYearA11yLabel: 'MMMM yyyy'},
};



export const appConfig: ApplicationConfig = {
  providers: [

    provideHttpClient(
      withInterceptors([includeBearerTokenInterceptor])
    ),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: '/assets/i18n/',
        suffix: '.json'
      }),
      fallbackLang: 'fr',
      lang: 'fr'
    }),
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [
        createInterceptorCondition({
          urlPattern: /\/api\//i,
          bearerPrefix: 'Bearer'
        })
      ]
    },
    provideRouter(routes),
    provideCharts(withDefaultRegisterables()),
    provideLuxonDateAdapter(YEARMONTH_FORMATS),
    AutoRefreshTokenService,
    UserActivityService,
    provideKeycloak({
      config: {
        url: environment.authServer,
        realm: 'befintax',
        clientId: 'befintax-frontend'
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
      },
      features: [
        withAutoRefreshToken(),
      ],
    }),
  ]
};



