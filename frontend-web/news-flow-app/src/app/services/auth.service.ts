import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userRole: string | null = null;
  private username: string | null = null;

  // Simulate a database of users
  private users = [
    { username: 'manav', password: 'pxl', role: 'Editor' },
    { username: 'john', password: 'pxl', role: 'User' },
    { username: 'sofia', password: 'pxl', role: 'Editor' }
  ];

  constructor() {}

  // Simulate login
  login(username: string, password: string): boolean {
    const user = this.users.find(u => u.username === username && u.password === password);

    if (user) {
      this.userRole = user.role;  // Assign the user's role
      this.username = username;
      sessionStorage.setItem('userRole', this.userRole);
      sessionStorage.setItem('username', this.username);
      return true;
    }

    return false;
  }

  getUserRole(): string | null {
    return this.userRole || localStorage.getItem('userRole');
  }

  getUsername(): string | null{
    return this.username || localStorage.getItem('username');
  }

  // Log out the user (clear session)
  logout(): void {
    this.userRole = null;
    sessionStorage.removeItem('userRole');
    sessionStorage.removeItem('username');
  }

  // Check if the user is logged in
  isLoggedIn(): boolean {
    return this.userRole !== null || localStorage.getItem('userRole') !== null;
  }
}
