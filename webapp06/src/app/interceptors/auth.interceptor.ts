import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(
        private router: Router,
        private authService: AuthService
    ) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // Asegurarse de que las credenciales (cookies) se envíen con cada solicitud
        const authReq = req.clone({
            withCredentials: true
        });

        return next.handle(authReq).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 401) {
                    // Si recibimos un 401 Unauthorized, significa que el usuario no está autenticado
                    // o que la sesión ha expirado
                    this.authService.logout();
                    this.router.navigate(['/login'], {
                        queryParams: { returnUrl: this.router.url }
                    });
                } else if (error.status === 403) {
                    // Si recibimos un 403 Forbidden, el usuario no tiene permisos suficientes
                    this.router.navigate(['/forbidden']);
                }
                return throwError(error);
            })
        );
    }
}