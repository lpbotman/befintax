import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import Keycloak from 'keycloak-js';



export const authGuard: CanActivateFn = async (route, state) => {
  const keycloak = inject(Keycloak); // On injecte l'instance JS native

  // Vérification simple via la propriété native
  if (keycloak.authenticated) {
    return true;
  }

  // Redirection vers le login
  await keycloak.login({
    redirectUri: window.location.origin + state.url
  });

  return false;
};

