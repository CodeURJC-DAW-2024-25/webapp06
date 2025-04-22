import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from './auth.service';

// Update these interfaces as needed to match your backend models
interface CartItem {
    id: number;
    productId: number;
    quantity: number;
    product: any;
}

interface Cart {
    id: number;
    userId: number;
    items: CartItem[];
    totalPrice: number;
}

@Injectable({
    providedIn: 'root'
})
export class ShoppingCartService {
    private apiUrl = 'https://localhost:8443/api/cart';
    private cartSubject = new BehaviorSubject<Cart | null>(null);

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {
        this.loadCart();
    }

    getCart(): Observable<Cart | null> {
        return this.cartSubject.asObservable();
    }

    // Renamed from loadCart to getShoppingCart to match component usage
    getShoppingCart(): Observable<Cart> {
        const userId = this.authService.getCurrentUser()?.id;
        if (!userId) {
            throw new Error('User is not authenticated');
        }

        return this.http.get<Cart>(`${this.apiUrl}/user/${userId}`);
    }

    loadCart(): void {
        const userId = this.authService.getCurrentUser()?.id;
        if (userId) {
            this.getShoppingCart().subscribe({
                next: (cart) => this.cartSubject.next(cart),
                error: (err) => console.error('Error loading cart', err)
            });
        }
    }

    // Renamed from addToCart to addProductToCart to match component usage
    addProductToCart(productId: number, quantity: number = 1): Observable<Cart> {
        const userId = this.authService.getCurrentUser()?.id;
        if (!userId) {
            throw new Error('User is not authenticated');
        }

        return this.http.post<Cart>(`${this.apiUrl}/add`, { userId, productId, quantity });
    }

    // Keep the original method for compatibility
    addToCart(productId: number, quantity: number = 1): Observable<Cart> {
        return this.addProductToCart(productId, quantity);
    }

    // Renamed from removeFromCart to removeProductFromCart to match component usage
    removeProductFromCart(itemId: number): Observable<Cart> {
        const userId = this.authService.getCurrentUser()?.id;
        if (!userId) {
            throw new Error('User is not authenticated');
        }

        return this.http.delete<Cart>(`${this.apiUrl}/remove/${itemId}?userId=${userId}`);
    }

    // Keep the original method for compatibility
    removeFromCart(itemId: number): Observable<Cart> {
        return this.removeProductFromCart(itemId);
    }

    updateQuantity(itemId: number, quantity: number): Observable<Cart> {
        const userId = this.authService.getCurrentUser()?.id;
        if (!userId) {
            throw new Error('User is not authenticated');
        }

        return this.http.put<Cart>(`${this.apiUrl}/update/${itemId}`, { userId, quantity });
    }

    clearCart(): Observable<any> {
        const userId = this.authService.getCurrentUser()?.id;
        if (!userId) {
            throw new Error('User is not authenticated');
        }

        return this.http.delete<any>(`${this.apiUrl}/clear?userId=${userId}`);
    }

    // Add the processPayment method
    processPayment(): Observable<any> {
        const userId = this.authService.getCurrentUser()?.id;
        if (!userId) {
            throw new Error('User is not authenticated');
        }

        return this.http.post<any>(`${this.apiUrl}/checkout`, { userId });
    }
}