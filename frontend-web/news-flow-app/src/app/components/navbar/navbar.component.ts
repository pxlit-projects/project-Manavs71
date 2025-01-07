import { Component } from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  username: string | null;
  userRole: string | null;
  constructor(private authService: AuthService, private router: Router) {
    this.username = authService.getUsername();
    this.userRole = authService.getUserRole();
  }

  logout(): void {
    this.authService.logout(); // Call the AuthService's logout method
    this.router.navigate(['/login']); // Redirect to the login page
  }




}
