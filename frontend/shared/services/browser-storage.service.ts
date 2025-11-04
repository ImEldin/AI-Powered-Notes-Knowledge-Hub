import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class BrowserStorageService {
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  setItem(key: string, value: string) {
    if (this.isBrowser) {
      localStorage.setItem(key, value);
    }
  }

  getItem(key: string) {
    if (this.isBrowser) {
      return localStorage.getItem(key);
    }
    return null;
  }

  removeItem(key: string) {
    if (this.isBrowser) {
      localStorage.removeItem(key);
    }
  }

  clear() {
    if (this.isBrowser) {
      localStorage.clear();
    }
  }

  setSessionItem(key: string, value: string) {
    if (this.isBrowser) {
      sessionStorage.setItem(key, value);
    }
  }

  getSessionItem(key: string) {
    if (this.isBrowser) {
      return sessionStorage.getItem(key);
    }
    return null;
  }

  removeSessionItem(key: string) {
    if (this.isBrowser) {
      sessionStorage.removeItem(key);
    }
  }

  clearSession() {
    if (this.isBrowser) {
      sessionStorage.clear();
    }
  }

  isBrowserPlatform() {
    return this.isBrowser;
  }
}
