import { Injectable, signal, computed, inject } from '@angular/core';
import Keycloak from 'keycloak-js';

export interface UserProfile {
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private keycloak = inject(Keycloak);
  private currentLang = 'fr';

  userProfile = signal<UserProfile | null>(null);

  isLoggedIn = computed(() => this.userProfile() !== null);

  constructor() {
    this.loadUserProfile();
  }

  async loadUserProfile() {
    if (this.keycloak.authenticated) {
      const profile = await this.keycloak.loadUserProfile();

      this.userProfile.set({
        username: profile.username ?? '',
        email: profile.email ?? '',
        firstName: profile.firstName,
        lastName: profile.lastName
      });
    } else {
      this.userProfile.set(null);
    }
  }

  login() {
    this.keycloak.login();
  }

  register() {
    this.keycloak.register();
  }

  logout() {
    this.keycloak.logout();
    this.userProfile.set(null);
  }

  accountManagement() {
    this.keycloak.accountManagement();
  }
}
