import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isLoggedIn()) {
      const userRole = this.authService.getUserRole();

      // If the user is not logged in or the route is not allowed for their role, redirect to login
      if (userRole === 'User' && state.url !== '/published') {
        this.router.navigate(['/published']); // Users can only access the "published" page
        return false;
      }

      // Editors can access all routes
      if (userRole === 'Editor') {
        return true;
      }

      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
