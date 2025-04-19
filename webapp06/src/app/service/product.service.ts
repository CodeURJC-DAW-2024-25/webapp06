import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../enviroments/enviroment';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private apiUrl = `${environment.apiUrl}/products`;

    constructor(
        private http: HttpClient
    ) { }

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
        return this.http.get(`${this.apiUrl}/type/${type}`, { params, withCredentials: true });
    }

    // Obtener un producto por ID
    getProductById(id: number): Observable<any> {
        return this.http.get(`${this.apiUrl}/${id}`, { withCredentials: true });
    }

    // Obtener los productos más vistos
    getMostViewedProducts(): Observable<any> {
        return this.http.get(`${this.apiUrl}/mostViewedProducts`, { withCredentials: true });
    }

    // Obtener los últimos productos añadidos
    getLastProducts(): Observable<any> {
        return this.http.get(`${this.apiUrl}/lastProducts`, { withCredentials: true });
    }
}