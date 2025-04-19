// src/app/services/product.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiConfigService } from './api-config.service';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private apiUrl: string;

    constructor(
        private http: HttpClient,
        private apiConfigService: ApiConfigService
    ) {
        this.apiUrl = `${this.apiConfigService.getApiBaseUrl()}/products`;
    }

    // Obtener todos los productos (paginados)
    getProducts(page: number = 0, size: number = 5): Observable<any> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get(`${this.apiUrl}/`, { params });
    }

    // Obtener productos por tipo
    getProductsByType(type: string, page: number = 0, size: number = 5): Observable<any> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get(`${this.apiUrl}/type/${type}`, { params });
    }

    // Obtener un producto por ID
    getProductById(id: number): Observable<any> {
        return this.http.get(`${this.apiUrl}/${id}`);
    }

    // Obtener productos aceptados
    getAcceptedProducts(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/acceptedProducts`);
    }

    // Obtener los productos más vistos
    getMostViewedProducts(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/mostViewedProducts`);
    }

    // Obtener los últimos productos
    getLastProducts(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/lastProducts`);
    }
}