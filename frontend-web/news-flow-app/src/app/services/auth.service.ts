import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userRole: string | null = null;

  constructor() {}

  // Simulate login (you can replace this with actual authentication logic)
  login(username: string, password: string): boolean {

    if (username === 'manav' && password === 'pxl') {
      this.userRole = 'Redacteur';  // Simulate assigning a role
      localStorage.setItem('userRole', this.userRole);
      return true;
    }
    return false;
  }

  getUserRole(): string | null {
    return this.userRole || localStorage.getItem('userRole');
  }

  // Log out the user (clear session)
  logout(): void {
    this.userRole = null;
    localStorage.removeItem('userRole');
  }

  // Check if the user is logged in
  isLoggedIn(): boolean {
    return this.userRole !== null || localStorage.getItem('userRole') !== null;
  }
}
