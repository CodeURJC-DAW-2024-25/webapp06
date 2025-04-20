import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, from, switchMap, map } from 'rxjs'; // Añadir 'map' a las importaciones
import { environment } from '../enviroments/enviroment';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private apiUrl = `${environment.apiUrl}/products`;

    constructor(private http: HttpClient) { }

    getProducts(page: number = 0, size: number = 5): Observable<any> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get(`${this.apiUrl}/`, { params })
            .pipe(
                map((response: any) => this.processProductsResponse(response)) // Corregido 'Map' a 'map' y añadido tipo
            );
    }

    getProductsByType(type: string, page: number = 0, size: number = 5): Observable<any> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get(`${this.apiUrl}/type/${type}`, { params })
            .pipe(
                map((response: any) => this.processProductsResponse(response)) // Añadido tipo
            );
    }

    getProductById(id: number): Observable<any> {
        return this.http.get(`${this.apiUrl}/${id}`)
            .pipe(
                map((product: any) => this.processProductResponse(product)) // Añadido tipo
            );
    }

    /**
     * Procesa la respuesta de la API con múltiples productos
     * y carga las imágenes para cada uno de ellos
     */
    private processProductsResponse(response: any): any {
        if (response && response.content) {
            // Para cada producto en la respuesta, cargar su imagen
            response.content = response.content.map((product: any) => {
                return this.processProductResponse(product);
            });
        }
        return response;
    }

    /**
     * Procesa un único producto y carga su imagen
     */
    private processProductResponse(product: any): any {
        if (product && product.id && !product.imageBase64) {
            // Si el producto no tiene ya una imagen en base64, cargarla
            this.loadProductImage(product.id)
                .subscribe(imageBase64 => {
                    product.imageBase64 = imageBase64;
                });
        }
        return product;
    }

    /**
     * Carga la imagen de un producto por su ID
     */
    loadProductImage(productId: number): Observable<string> {
        return this.http.get(`${this.apiUrl}/${productId}/image`, { responseType: 'blob' })
            .pipe(
                switchMap(blob => from(this.convertBlobToBase64(blob)))
            );
    }

    /**
     * Convierte un Blob a string Base64
     */
    private convertBlobToBase64(blob: Blob): Promise<string> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result as string);
            reader.onerror = reject;
            reader.readAsDataURL(blob);
        });
    }
}