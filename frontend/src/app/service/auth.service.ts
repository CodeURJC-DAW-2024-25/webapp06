import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError, map, switchMap } from 'rxjs/operators';

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
                switchMap(() => {
                    // Realizar la segunda solicitud para obtener el perfil del usuario
                    return this.http.get<any>(`${this.apiUrl}/main/profile`);}),
                map(profile => {
                    console.log('Usuario obtenido del perfil:', profile);

                    // Si no hay respuesta, crear una respuesta temporal con el nombre de usuario
                    if (profile) {
                        profile = {
                            // Generar un token fake solo para desarrollo
                            token: `fake_token_${new Date().getTime()}`,
                            user: {
                                id: profile.id,
                                name: profile.name,
                                username: profile.username,
                                email: profile.email,
                                roles: profile.role, // Asumimos que "role" es un array
                                orders: profile.orders,
                                reviews: profile.reviews,
                                cart: profile.cart,
                                cartPrice: profile.cartPrice,
                                historicalOrderPrices: profile.historicalOrderPrices
                            }
                        };
                        console.log('Generando respuesta temporal:', profile);
                    }

                    return profile;
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
                            const basicUser = { username: username, roles: response.roles };
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
            localStorage.removeItem(this.tokenKey);
            localStorage.removeItem('user');
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