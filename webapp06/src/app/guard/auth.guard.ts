import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        if (this.authService.isLoggedIn()) {
            // Verificar roles si se necesita
            const requiredRole = route.data['role'] as string;
            if (!requiredRole || this.authService.hasRole(requiredRole)) {
                return true;
            }

            // Si requiere un rol que el usuario no tiene
            this.router.navigate(['/forbidden']);
            return false;
        }

        // Guardar la URL a la que se intentaba acceder para redireccionar después del login
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return false;
    }
}