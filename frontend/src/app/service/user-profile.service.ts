import { Injectable } from "@angular/core";
import { environment } from "../enviroments/enviroment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, switchMap, tap } from "rxjs/operators";
import { AuthService } from "./auth/auth.service";
import { Observable } from "rxjs";



@Injectable({
    providedIn: 'root'
})

export class UserProfileService {
    private apiUrl = `${environment.apiUrl}/users`

    constructor(private http: HttpClient, private authService: AuthService) { }

    getUserInfo(userId: string): Observable<any> {

        return this.http.get(`${this.apiUrl}/${userId}`)
    }


    editProfile(userData: any): Observable<any> {
        console.log("Username" + userData.username)
        console.log("Name " + userData.name)
        console.log("Email " + userData.email)

        return this.http.put(`${this.apiUrl}/update-profile`, userData).pipe(
            switchMap(() => this.authService.refreshToken()),
            tap(() => console.log("Usuarioo service profile ", localStorage.getItem('user')))
        );

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