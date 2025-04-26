import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth/auth.service';
import { Subscription } from 'rxjs';

interface User {
  username: string;
  roles: string[] | string;
  // otros campos
}

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css'],
  standalone: false
})
export class NavComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  logged = false; // Added to match template
  username: string | null = null;
  isAdmin = false;
  isCompany = false;
  isUser = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Suscribirse al estado de autenticación
    this.subscriptions.push(
      this.authService.isLoggedIn$.subscribe(loggedIn => {
        console.log('NavComponent - Estado de autenticación actualizado:', loggedIn);
        this.isLoggedIn = loggedIn;
        this.logged = loggedIn; // Actualizar ambas propiedades
      })
    );

    // Suscribirse a cambios en el usuario
    this.subscriptions.push(
      this.authService.user$.subscribe((user: User | null) => {
        console.log('NavComponent - Usuario actualizado:', user);

        if (user) {
          this.username = user.username;

          // Determinar roles
          if (typeof user.roles === 'string') {
            this.isAdmin = user.roles === 'ADMIN';
            this.isCompany = user.roles === 'COMPANY';
            this.isUser = user.roles === 'USER' || !this.isCompany;
          } else if (Array.isArray(user.roles)) {
            this.isAdmin = user.roles.includes('ADMIN');
            this.isCompany = user.roles.includes('COMPANY');
            this.isUser = user.roles.includes('USER') || (!this.isAdmin && !this.isCompany);
          }
        } else {
          this.username = null;
          this.isAdmin = false;
          this.isCompany = false;
          this.isUser = false;
        }

        console.log('NavComponent - Propiedades actualizadas:', {
          username: this.username,
          isAdmin: this.isAdmin,
          isCompany: this.isCompany,
          isUser: this.isUser
        });
      })
    );

    // Verificar el estado inicial
    const currentUser = this.authService.getCurrentUser();
    const isLogged = this.authService.getToken() !== null;
    console.log('NavComponent - Estado inicial:', {
      currentUser,
      isLogged,
      storedToken: localStorage.getItem('auth_token'),
      storedUser: localStorage.getItem('user')
    });
  }

  ngOnDestroy(): void {
    // Limpiar suscripciones para prevenir memory leaks
    this.subscriptions.forEach(sub => sub.unsubscribe());
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