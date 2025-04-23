import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = '/v1/api'; // Asegúrate de que coincida con tu backend
    private tokenKey = 'auth_token';
    private userSubject: BehaviorSubject<any>;
    public user: Observable<any>;
    public user$: Observable<any>; // Alias para compatibilidad
    private isLoggedInSubject = new BehaviorSubject<boolean>(false);
    isLoggedIn$ = this.isLoggedInSubject.asObservable();

    constructor(private http: HttpClient) {
        // Comprobar si hay información de sesión guardada
        const storedToken = localStorage.getItem(this.tokenKey);
        const storedUser = localStorage.getItem('user');

        this.userSubject = new BehaviorSubject<any>(storedUser ? JSON.parse(storedUser) : null);
        this.user = this.userSubject.asObservable();
        this.user$ = this.user; // Compatibilidad

        // Actualizar el estado de inicio de sesión basado en la existencia del token
        this.isLoggedInSubject.next(!!storedToken);

        // Log para depurar el estado inicial
        console.log('Estado inicial de autenticación:', {
            token: !!storedToken,
            user: storedUser ? JSON.parse(storedUser) : null,
            isLoggedIn: this.isLoggedInSubject.value
        });
    }

    login(username: string, password: string): Observable<any> {
        console.log('Intentando login con:', username);

        // POST al endpoint de autenticación
        return this.http.post<any>(`${this.apiUrl}/auth/login`, { username, password })
            .pipe(
                map(response => {
                    console.log('Respuesta del login (raw):', response);

                    // Si no hay respuesta, crear una respuesta temporal con el nombre de usuario
                    if (!response) {
                        response = {
                            // Generar un token fake solo para desarrollo
                            token: `fake_token_${new Date().getTime()}`,
                            user: {
                                username: username,
                                roles: ['USER'] // Rol por defecto
                            }
                        };
                        console.log('Generando respuesta temporal:', response);
                    }

                    return response;
                }),
                tap(response => {
                    console.log('Procesando respuesta de login:', response);

                    // Asegurarse de que hay una respuesta con token antes de almacenar
                    if (response && response.token) {
                        // Guardar el token
                        localStorage.setItem(this.tokenKey, response.token);

                        // Guardar datos del usuario si existen
                        if (response.user) {
                            localStorage.setItem('user', JSON.stringify(response.user));
                            this.userSubject.next(response.user);
                        } else {
                            // Si no hay datos de usuario pero sí token, crear un usuario básico
                            const basicUser = { username: username, roles: ['USER'] };
                            localStorage.setItem('user', JSON.stringify(basicUser));
                            this.userSubject.next(basicUser);
                        }

                        // Actualizar el estado de inicio de sesión
                        this.isLoggedInSubject.next(true);
                        console.log('Estado de autenticación actualizado:', {
                            isLoggedIn: true,
                            user: this.userSubject.value
                        });
                    }
                }),
                catchError(error => {
                    console.error('Error en la solicitud de login:', error);
                    throw error;
                })
            );
    }

    // Métodos necesarios para compatibilidad
    isLoggedIn(): Observable<boolean> {
        return this.isLoggedIn$;
    }

    getCurrentUser(): any {
        return this.userSubject.value;
    }

    getUserIdByUsername(username: string): Observable<number> {
        console.log('Solicitando ID para el usuario:', username);
        return this.http.get<number>(`${this.apiUrl}/users/by-username/${username}`)
            .pipe(
                tap(id => console.log('ID obtenido del backend:', id)),
                catchError(error => {
                    console.error('Error al obtener el ID del usuario:', error);
                    throw error;
                })
            );
    }

    getCurrentUserId(): Observable<number | null> {
        const user = this.getCurrentUser(); // obtengo el usuario actual
        if (!user) return of(null);

        if (user.id) return of(user.id);

        if (user.username) { // en caso de solo tener el username
            return this.getUserIdByUsername(user.username).pipe( // obtengo el id
                tap(id => {
                    user.id = id;
                    localStorage.setItem('user', JSON.stringify(user));
                    this.userSubject.next({ ...user });
                })
            );
        }

        return of(null);
    }

    hasRole(role: string): boolean {
        const user = this.getCurrentUser();
        return user && user.roles &&
            (Array.isArray(user.roles) ? user.roles.includes(role) : user.roles === role);
    }

    register(username: string, email: string, password: string, role: string = 'USER'): Observable<any> {
        return this.http.post(`${this.apiUrl}/users/`, {
            username,
            email,
            password,
            roles: [role]
        });
    }

    logout(): Observable<void> {
        return new Observable<void>((observer) => {
            console.log('Cerrando sesión...');

            // Limpiar datos de autenticación
            localStorage.removeItem(this.tokenKey);
            localStorage.removeItem('user');

            // Asegurarse de que el estado se actualiza correctamente
            this.userSubject.next(null);
            this.isLoggedInSubject.next(false);

            console.log('Sesión cerrada');
            observer.next();
            observer.complete();
        });
    }

    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }
}