import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, from, switchMap, map, catchError } from 'rxjs'; // Añadir 'map' a las importaciones
import { environment } from '../enviroments/enviroment';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private apiUrl = `${environment.apiUrl}/products`;
    private product: any; // Variable para almacenar el producto

    constructor(private http: HttpClient) { }

    getProducts(page: number = 0, size: number = 5, params?: { accepted?: boolean; company?: string }): Observable<any> {
        let queryParams: any = {
            page: page.toString(),
            size: size.toString(),
        };

        // Agregar parámetros opcionales si están presentes
        if (params?.accepted !== undefined) {
            queryParams.accepted = params.accepted.toString();
        }
        if (params?.company) {
            queryParams.company = params.company;
        }

        return this.http.get<any>(`${this.apiUrl}/`, { params: queryParams }).pipe(
            map((response: any) => this.processProductsResponse(response)) // Procesar las imágenes
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
        return this.http.get<any>(`${this.apiUrl}/${id}`);
    }

    getMostViewedProducts(): Observable<any[]> {
        return this.http.get(`${this.apiUrl}/mostViewedProducts`)
            .pipe(
                map((response: any) => this.processProductList(response))
            );

    }

    getLastProducts(): Observable<any[]> {
        return this.http.get(`${this.apiUrl}/lastProducts`)
            .pipe(
                map((response: any) => this.processProductList(response))
            );

    }

    createProduct(product: any): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/products/`, product);
    }

    updateProduct(id: number, product: any): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/${id}`, product);
    }

    deleteProduct(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/${id}`);
    }
    setProduct(product: any): void {
        this.product = product;
    }

    getProduct(): any {
        return this.product;
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
     * Procesa una lista de productos y carga sus imágenes
     */
    processProductList(products: any[]): any[] {
        return products.map(product => this.processProductResponse(product));
    }

    /**
     * Carga la imagen de un producto por su ID
     */
    loadProductImage(productId: number): Observable<string> {
        return this.http.get(`${this.apiUrl}/${productId}/image`, { responseType: 'blob' }).pipe(
            switchMap((blob) => this.convertBlobToBase64(blob))
        );
    }

    private convertBlobToBase64(blob: Blob): Observable<string> {
        return new Observable((observer) => {
            const reader = new FileReader();
            reader.onloadend = () => {
                observer.next(reader.result as string);
                observer.complete();
            };
            reader.onerror = (error) => {
                observer.error(error);
            };
            reader.readAsDataURL(blob);
        });
    }

    searchProducts(
        query: string,
        category?: string,
        minPrice?: number,
        maxPrice?: number
    ): Observable<any[]> {
        let params = new HttpParams();

        if (query) {
            params = params.append('query', query);
        }

        if (category) {
            params = params.append('category', category);
        }

        if (minPrice !== undefined) {
            params = params.append('minPrice', minPrice.toString());
        }

        if (maxPrice !== undefined) {
            params = params.append('maxPrice', maxPrice.toString());
        }

        return this.http.get<any[]>(`${this.apiUrl}/search`, { params }).pipe(
            catchError(error => {
                console.error('Error searching products:', error);
                throw error;
            })
        );
    }

    addReview(productId: number, review: { rating: number; comment: string }): Observable<any> {
        return this.http.post<any>(`${environment.apiUrl}/reviews/${productId}`, review);
    }

    getProductsNotAccepted(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/?accepted=false`);
    }

    acceptProduct(productId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/accept`, null, {
            params: { id: productId.toString() }
        });
    }

    declineProduct(productId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/decline`, null, {
            params: { id: productId.toString() }
        });
    }
}

function tap(arg0: (products: any[]) => void): import("rxjs").OperatorFunction<any, any[]> {
    throw new Error('Function not implemented.');
}
