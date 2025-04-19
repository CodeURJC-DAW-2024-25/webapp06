// src/app/services/shopping-cart.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiConfigService } from './api-config.service';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class ShoppingCartService {
    private apiUrl: string;

    constructor(
        private http: HttpClient,
        private apiConfigService: ApiConfigService,
        private authService: AuthService
    ) {
        this.apiUrl = `${this.apiConfigService.getApiBaseUrl()}/users`;
    }

    getShoppingCart(): Observable<any> {
        const userId = this.authService.getCurrentUser()?.id;
        return this.http.get(`${this.apiUrl}/${userId}/shoppingcarts`, { withCredentials: true });
    }

    addProductToCart(productId: number): Observable<any> {
        const userId = this.authService.getCurrentUser()?.id;
        return this.http.post(`${this.apiUrl}/${userId}/shoppingcarts/${productId}`, {}, { withCredentials: true });
    }

    removeProductFromCart(productId: number): Observable<any> {
        const userId = this.authService.getCurrentUser()?.id;
        return this.http.delete(`${this.apiUrl}/${userId}/shoppingcarts/${productId}`, { withCredentials: true });
    }

    processPayment(): Observable<any> {
        const userId = this.authService.getCurrentUser()?.id;
        return this.http.post(`${this.apiUrl}/${userId}/processPayment`, {}, { withCredentials: true });
    }
}