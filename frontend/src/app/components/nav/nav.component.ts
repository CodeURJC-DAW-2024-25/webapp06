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
  logged = false;
  username: string | null = null;
  isAdmin = false;
  isCompany = false;
  isUser = false;

  searchText: string = '';
  searchType: string = 'all';

  private subscriptions: Subscription[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Suscripción al estado de autenticación
    const authSub = this.authService.isLoggedIn$.subscribe(isLoggedIn => {
      console.log('Estado de autenticación:', isLoggedIn);
      this.isLoggedIn = isLoggedIn;
      this.logged = isLoggedIn;

      // Si está autenticado, obtener información del usuario
      if (isLoggedIn) {
        // Primero, intentar cargar el usuario desde localStorage
        try {
          const userStr = localStorage.getItem('user');
          if (userStr) {
            const user = JSON.parse(userStr);
            this.processUserData(user);
          }
        } catch (e) {
          console.error('Error al cargar usuario desde localStorage:', e);
        }

        // Luego, suscribirse a cambios en el usuario
        const userSub = this.authService.user$.subscribe(user => {
          if (user) {
            this.processUserData(user);
          }
        });
        this.subscriptions.push(userSub);
      } else {
        // Restablecer valores si no está autenticado
        this.isCompany = false;
        this.isAdmin = false;
        this.isUser = true; // Por defecto mostrar como usuario normal
        this.username = '';
      }
    });
    this.subscriptions.push(authSub);
  }

  // Nuevo método para procesar datos del usuario
  private processUserData(user: any): void {
    console.log('Procesando datos de usuario en NavComponent:', user);
    this.username = user.username;

    // Verificar el rol directamente en cualquier formato
    let foundCompanyRole = false;
    let foundAdminRole = false;

    // Buscar en user.roles (formato array)
    if (user.roles && Array.isArray(user.roles)) {
      foundCompanyRole = user.roles.some((r: any) =>
        typeof r === 'string' && r.toLowerCase() === 'company'
      );
      foundAdminRole = user.roles.some((r: any) =>
        typeof r === 'string' && r.toLowerCase() === 'admin'
      );
    }

    // Buscar en user.roles (formato string)
    if (!foundCompanyRole && !foundAdminRole && user.roles && typeof user.roles === 'string') {
      foundCompanyRole = user.roles.toLowerCase() === 'company';
      foundAdminRole = user.roles.toLowerCase() === 'admin';
    }

    // Buscar en user.role como respaldo (formato array)
    if (!foundCompanyRole && !foundAdminRole && user.role && Array.isArray(user.role)) {
      foundCompanyRole = user.role.some((r: any) =>
        typeof r === 'string' && r.toLowerCase() === 'company'
      );
      foundAdminRole = user.role.some((r: any) =>
        typeof r === 'string' && r.toLowerCase() === 'admin'
      );
    }

    // Buscar en user.role como respaldo (formato string)
    if (!foundCompanyRole && !foundAdminRole && user.role && typeof user.role === 'string') {
      foundCompanyRole = user.role.toLowerCase() === 'company';
      foundAdminRole = user.role.toLowerCase() === 'admin';
    }

    // Usar hasRole como último recurso
    if (!foundCompanyRole) {
      foundCompanyRole = this.authService.hasRole('COMPANY');
    }
    if (!foundAdminRole) {
      foundAdminRole = this.authService.hasRole('ADMIN');
    }

    this.isCompany = foundCompanyRole;
    this.isAdmin = foundAdminRole;
    this.isUser = !foundCompanyRole && !foundAdminRole;

    console.log('Roles detectados finalmente:', {
      isCompany: this.isCompany,
      isAdmin: this.isAdmin,
      isUser: this.isUser
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

  onSearch(): void {
    // Navega al componente de búsqueda con parámetros query
    this.router.navigate(['/search'], {
      queryParams: {
        search_text: this.searchText,
        type: this.searchType
      }
    });
  }

}