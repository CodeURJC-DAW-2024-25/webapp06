import { Injectable } from "@angular/core";
import { environment } from "../enviroments/enviroment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, switchMap, tap } from "rxjs/operators";
import { AuthService } from "./auth/auth.service";
import { Observable, of } from "rxjs";



@Injectable({
    providedIn: 'root'
})

export class UserProfileService {
    private apiUrl = `${environment.apiUrl}/users`

    constructor(private http: HttpClient, private authService: AuthService) { }

    getUserInfo(userId: string): Observable<any> {

        return this.http.get(`${this.apiUrl}/${userId}`)
    }

    getUserProfile(): Observable<any> {
        return this.http.get(`${this.apiUrl}/profile`);
    }

    editProfile(userData: any): Observable<any> {
        console.log("Updating profile with data:", userData);

        return this.http.put(`${this.apiUrl}/update-profile`, userData);
    }


    // editProfile(userData: any): Observable<any> {
    //     const currentUser = this.authService.getCurrentUser();
    //     const usernameChanged = currentUser && userData.username && currentUser.username !== userData.username;

    //     console.log("Username: " + userData.username);
    //     console.log("Name: " + userData.name);
    //     console.log("Email: " + userData.email);
    //     console.log("Username changed: " + usernameChanged);

    //     return this.http.put(`${this.apiUrl}/update-profile`, userData).pipe(
    //         switchMap(updatedUser => {
    //             if (usernameChanged) {
    //                 // If username changed, we need to log in with the new credentials
    //                 // Assuming we have the password (you might need to ask for it)
    //                 console.log("Username changed, logging in with new credentials");

    //                 // Option 1: Force logout and redirect to login page
    //                 this.authService.logout().subscribe(() => {
    //                     // You might want to show a message explaining why they need to log in again
    //                     window.location.href = '/login?message=username_changed';
    //                 });

    //                 return of(updatedUser);

    //                 // Option 2 (if you have password): Auto re-login
    //                 // return this.authService.login(userData.username, storedPassword);
    //             } else {
    //                 return this.authService.refreshToken().pipe(
    //                     tap(() => console.log("User profile updated: ", localStorage.getItem('user')))
    //                 );
    //             }
    //         })
    //     );
    // }

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

    // getUserProfile(): Observable<any> {
    //     return this.http.get('/v1/api/users/profile');
    // }

    getUserById(id: number): Observable<any> {
        return this.http.get(`/v1/api/users/${id}`);
    }

  uploadUserImage(userId: number, formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/image`, formData);
  }

  updateUserImage(userId: number, formData: FormData): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/image`, formData);
  }

  updateUserProfile(userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update-profile`, userData, { observe: 'response' });
  }
}