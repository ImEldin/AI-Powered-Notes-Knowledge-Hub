import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  private isAuthenticatedSubject: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);
  public isAuthenticated$: Observable<boolean> =
    this.isAuthenticatedSubject.asObservable();

  constructor() {}

  setAuthenticated(value: boolean): void {
    this.isAuthenticatedSubject.next(value);
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }
}
