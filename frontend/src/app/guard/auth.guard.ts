import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../service/auth/auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {
    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): boolean {
        // Check if user is logged in
        if (!this.authService.getToken()) {
            this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
            return false;
        }

        // Check for required role if specified
        const requiredRole = route.data['requiredRole'];
        if (!requiredRole || this.authService.hasRole(requiredRole)) {
            return true;
        }

        // User doesn't have required role
        this.router.navigate(['/']);
        return false;
    }
}