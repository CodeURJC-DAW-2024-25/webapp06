import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'https://localhost:8443/v1/api';
    private userSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
    public user$ = this.userSubject.asObservable();

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        // Intentar cargar el usuario al iniciar la aplicación
        this.loadUserProfile();
    }

    /**
     * Inicia sesión con un nombre de usuario y contraseña
     */
    login(username: string, password: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/auth/login`, { username, password }, { withCredentials: true })
            .pipe(
                tap(() => {
                    this.loadUserProfile();
                })
            );
    }

    /**
     * Cierra la sesión del usuario actual
     */
    logout(): Observable<any> {
        return this.http.post(`${this.apiUrl}/auth/logout`, {}, { withCredentials: true })
            .pipe(
                tap(() => {
                    this.userSubject.next(null);
                    this.router.navigate(['/login']);
                })
            );
    }

    /**
     * Registra un nuevo usuario
     */
    register(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/auth/register`, userData);
    }

    /**
     * Carga el perfil del usuario desde el backend si está autenticado
     */
    loadUserProfile(): void {
        this.http.get(`${this.apiUrl}/users/profile`, { withCredentials: true })
            .subscribe(
                (user) => {
                    this.userSubject.next(user);
                },
                (error) => {
                    console.error('Error loading user profile', error);
                    this.userSubject.next(null);
                }
            );
    }

    /**
     * Comprueba si el usuario ha iniciado sesión
     */
    isLoggedIn(): boolean {
        return this.userSubject.value !== null;
    }

    /**
     * Obtiene el usuario actual
     */
    getCurrentUser(): any {
        return this.userSubject.value;
    }

    /**
     * Comprueba si el usuario tiene un rol específico
     */
    hasRole(role: string): boolean {
        const user = this.userSubject.value;
        if (!user || !user.roles) {
            return false;
        }
        return user.roles.some((r: string) => r === role);
    }

    /**
     * Actualiza la información del perfil del usuario
     */
    updateProfile(userData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/users/profile`, userData, { withCredentials: true })
            .pipe(
                tap((updatedUser) => {
                    this.userSubject.next(updatedUser);
                })
            );
    }
}