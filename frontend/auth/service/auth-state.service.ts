import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { BrowserStorageService } from '../../shared/services/browser-storage.service';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  private isAuthenticatedSubject: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);
  public isAuthenticated$: Observable<boolean> =
    this.isAuthenticatedSubject.asObservable();

  private authInitializedSubject: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);
  public authInitialized$: Observable<boolean> =
    this.authInitializedSubject.asObservable();

  private userRoleSubject: BehaviorSubject<string> =
    new BehaviorSubject<string>('USER');
  public userRole$: Observable<string> = this.userRoleSubject.asObservable();

  constructor(private storage: BrowserStorageService) {
    const storedRole = this.storage.getItem('userRole');
    if (storedRole) {
      this.userRoleSubject.next(storedRole);
    }
  }

  setAuthenticated(value: boolean) {
    this.isAuthenticatedSubject.next(value);
  }

  isLoggedIn() {
    return this.isAuthenticatedSubject.value;
  }

  setAuthInitialized(value: boolean) {
    this.authInitializedSubject.next(value);
  }

  isAuthInitialized() {
    return this.authInitializedSubject.value;
  }

  setUserRole(role: string) {
    this.userRoleSubject.next(role);
    this.storage.setItem('userRole', role);
  }

  getUserRole() {
    return this.userRoleSubject.value;
  }

  isAdmin() {
    return this.getUserRole() === 'ADMIN';
  }

  isUser() {
    return this.getUserRole() === 'USER';
  }

  clearRole() {
    this.userRoleSubject.next('USER');
    this.storage.removeItem('userRole');
  }

  clearAuthState() {
    this.setAuthenticated(false);
    this.setAuthInitialized(false);
    this.clearRole();

    this.storage.removeItem('emailVerified');
    this.storage.removeSessionItem('pendingVerificationEmail');
  }
}
