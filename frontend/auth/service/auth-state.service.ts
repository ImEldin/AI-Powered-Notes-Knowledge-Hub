import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;

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

  constructor() {
    this.isBrowser = isPlatformBrowser(this.platformId);

    if (this.isBrowser) {
      const storedRole = localStorage.getItem('userRole');
      if (storedRole) {
        this.userRoleSubject.next(storedRole);
      }
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
    if (this.isBrowser) {
      localStorage.setItem('userRole', role);
    }
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
    if (this.isBrowser) {
      localStorage.removeItem('userRole');
    }
  }
}
