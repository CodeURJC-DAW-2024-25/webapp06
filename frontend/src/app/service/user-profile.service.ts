import { Injectable } from "@angular/core";
import { environment } from "../enviroments/enviroment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable, switchMap } from "rxjs";
import { AuthService } from "./auth/auth.service";



@Injectable({
    providedIn: 'root'
})

export class UserProfileService {
    private apiUrl = `${environment.apiUrl}/users`

    constructor(private http: HttpClient) { }

    getUserInfo(userId: string): Observable<any> {

        return this.http.get(`${this.apiUrl}/${userId}`)
    }

    getUserImage(userId: string): Observable<string> {
        return this.http.get(`${this.apiUrl}/${userId}/image`, { responseType: 'blob' }).pipe(
            switchMap((blob) => {
                console.log('Blob recibido:', blob);
                return this.convertBlobToBase64(blob);
            })
        );
    }

    private convertBlobToBase64(blob: Blob): Observable<string> {
        return new Observable((observer) => {
            const reader = new FileReader();
            reader.onloadend = () => {
                console.log('Base64 generado:', reader.result);
                observer.next(reader.result as string);
                observer.complete();
            };
            reader.onerror = (error) => {
                console.error('Error al convertir Blob a Base64:', error);
                observer.error(error);
            };
            reader.readAsDataURL(blob);
        });
    }
}