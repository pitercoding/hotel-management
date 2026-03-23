import { Injectable } from '@angular/core';

const TOKEN = 'token';
const USER = 'user';

export interface StoredUser {
  id: number | string;
  role: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserStorageService {

  constructor() {}

  static saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN);
    window.localStorage.setItem(TOKEN, token);
  }

  static saveUser(user: StoredUser): void {
    window.localStorage.removeItem(USER);
    window.localStorage.setItem(USER, JSON.stringify(user));
  }

  static getToken(): string | null {
    return window.localStorage.getItem(TOKEN);
  }

  static getUser(): StoredUser | null {
    const raw = window.localStorage.getItem(USER);
    return raw ? (JSON.parse(raw) as StoredUser) : null;
  }

  static getUserId(): string {
    const user = this.getUser();
    if (user == null) { return ''; }
    return String(user.id);
  }

  static getUserRole(): string {
    const user = this.getUser();
    if (user == null) { return ''; }
    return user.role;
  }

  static isAdminLoggedIn(): boolean {
    if (this.getToken() == null) {
      return false;
    }

    const role: string = this.getUserRole();
    return role === 'ADMIN';
  }

  static isCustomerLoggedIn(): boolean {
    if (this.getToken() == null) {
      return false;
    }

    const role: string = this.getUserRole();
    return role === 'CUSTOMER';
  }

  static signOut(): void {
    window.localStorage.removeItem(TOKEN);
    window.localStorage.removeItem(USER);
  }

}
