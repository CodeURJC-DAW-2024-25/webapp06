import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

interface User {
  id: number;
  username: string;
  email: string;
  roles: string[];
}

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css'],
  standalone: false
})
export class NavComponent implements OnInit {
  isLoggedIn = false;
  logged = false; // Added to match template
  username: string | null = null;
  isAdmin = false;
  isCompany = false; // Added to match template
  isUser = false; // Added to match template

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.isLoggedIn().subscribe(loggedIn => {
      this.isLoggedIn = loggedIn;
      this.logged = loggedIn; // Update the 'logged' property as well
    });

    this.authService.user$.subscribe((user: User | null) => {
      this.username = user ? user.username : null;
      this.isAdmin = user ? user.roles.includes('ADMIN') : false;

      // Set user role flags
      if (user) {
        this.isCompany = user.roles.includes('COMPANY');
        this.isUser = user.roles.includes('USER') || !this.isCompany; // Default regular users if not company
      } else {
        this.isCompany = false;
        this.isUser = false;
      }
    });
  }

  getHomeLink(): string {
    // Return different home links based on user role
    if (this.isAdmin) {
      return '/admin/dashboard';
    } else if (this.isCompany) {
      return '/company/dashboard';
    } else {
      return '/';
    }
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}