import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule, CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (this.authService.login(this.username, this.password)) {
      // Redirect user based on their role
      const role = this.authService.getUserRole();
      if (role === 'Redacteur') {
        this.router.navigate(['/published']);  // Redirect to published posts for Redacteur
      } else {
        this.router.navigate(['/published']);  // Redirect to a different page based on role
      }
    } else {
      this.errorMessage = 'Invalid username or password';
    }
  }
}
