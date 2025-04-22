import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

interface User {
    id: number;
    username: string;
    email: string;
    roles: string[];
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    // URL base para las rutas de autenticación
    private apiUrl = '/v1/api';
    private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
    private currentUserSubject = new BehaviorSubject<User | null>(null);

    // Observable público para que los componentes puedan suscribirse
    user$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        // Cargar usuario desde localStorage si existe
        const userData = localStorage.getItem('currentUser');
        if (userData) {
            this.currentUserSubject.next(JSON.parse(userData));
        }
    }

    // Comprobar si hay un token en localStorage
    private hasToken(): boolean {
        return !!localStorage.getItem('token');
    }

    // Método para registrar un nuevo usuario
    register(username: string, email: string, password: string, role: string = 'USER'): Observable<any> {
        return this.http.post(`${this.apiUrl}/users/`, { username, email, password, roles: [role] });
    }

    // Método para iniciar sesión
    login(username: string, password: string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/auth/login`, { username, password })
            .pipe(
                tap(response => {
                    localStorage.setItem('token', response.token);
                    localStorage.setItem('currentUser', JSON.stringify(response.user));
                    this.isLoggedInSubject.next(true);
                    this.currentUserSubject.next(response.user);
                })
            );
    }

    // Método para cerrar sesión
    logout(): Observable<any> {
        // Eliminar token y datos de usuario
        localStorage.removeItem('token');
        localStorage.removeItem('currentUser');
        this.isLoggedInSubject.next(false);
        this.currentUserSubject.next(null);

        // Devolver un Observable que completa inmediatamente
        return of(null);
    }

    // Método para obtener el token de autenticación
    getToken(): string | null {
        return localStorage.getItem('token');
    }

    // Observable para saber si el usuario está autenticado
    isLoggedIn(): Observable<boolean> {
        return this.isLoggedInSubject.asObservable();
    }

    // Método para obtener el usuario actual
    getCurrentUser(): User | null {
        return this.currentUserSubject.value;
    }

    // Método para comprobar si el usuario tiene un rol específico
    hasRole(role: string): boolean {
        const user = this.getCurrentUser();
        return !!user && user.roles.includes(role);
    }
}