import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css'],
  standalone: false,
})
export class NavComponent implements OnInit {
  // Propiedades existentes
  isLoggedIn = false;
  currentUser: any;

  // Propiedades adicionales que necesita el template
  logged = false;
  isAdmin = false;
  isCompany = false;
  isUser = false;
  username = '';

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.logged = this.isLoggedIn; // Sincroniza 'logged' con 'isLoggedIn'
      this.currentUser = user;

      // Si hay un usuario, establece su nombre de usuario y rol
      if (user) {
        this.username = user.username || '';

        // Determinar el rol del usuario (ajusta según tu lógica de autenticación)
        this.isAdmin = user.role === 'ADMIN';
        this.isCompany = user.role === 'COMPANY';
        this.isUser = user.role === 'USER' || (!this.isAdmin && !this.isCompany);
      } else {
        this.username = '';
        this.isAdmin = false;
        this.isCompany = false;
        this.isUser = false;
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      // Reiniciar propiedades después del logout
      this.logged = false;
      this.isAdmin = false;
      this.isCompany = false;
      this.isUser = false;
      this.username = '';
    });
  }

  // Método para determinar el enlace de inicio según el rol
  getHomeLink(): string {
    return this.isAdmin ? '/adminPage' : '/';
  }
}