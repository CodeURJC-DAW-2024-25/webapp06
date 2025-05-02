import { Injectable } from "@angular/core";
import { environment } from "../enviroments/enviroment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { catchError, switchMap, tap } from "rxjs/operators";
import { AuthService } from "./auth/auth.service";



@Injectable({
    providedIn: 'root'
})

export class UserProfileService {
    private apiUrl = `${environment.apiUrl}/users`

    constructor(private http: HttpClient, private authService: AuthService) { }

    getUserInfo(userId: string): Observable<any> {

        return this.http.get(`${this.apiUrl}/${userId}`)
    }

    // editProfile(userData: any): Observable<any> {
    //     console.log("Username" + userData.username)
    //     console.log("Name " + userData.name)
    //     console.log("Email " + userData.email)

    //     return this.http.put(`${this.apiUrl}/update-profile`, userData).pipe(
    //         switchMap(() => this.authService.refreshToken()),
    //         tap(() => console.log("Usuarioo service profile ", localStorage.getItem('user')))
    //     );

    // }

    editProfile(userData: any): Observable<any> {
        console.log("Username: " + userData.username);
        console.log("Name: " + userData.name);
        console.log("Email: " + userData.email);
    
        return this.http.put(`${this.apiUrl}/update-profile`, userData).pipe(
            tap((response: any) => {
                // Verificar si el backend devuelve un nuevo token
                if (response && response.token) {
                    // Actualizar el token en localStorage
                    localStorage.setItem('auth_token', response.token);
    
                    // Decodificar el token para obtener los datos del usuario actualizado
                    const updatedUser = this.authService.decodeToken(response.token);
                    localStorage.setItem('user', JSON.stringify(updatedUser));
                    console.log("Usuario despues de guardar en el localstorage" + localStorage.getItem("user"))
    
                    // Actualizar el BehaviorSubject con los nuevos datos del usuario
                    // this.authService.updateCurrentUser(updatedUser);
    
                    console.log("Token y usuario actualizados en localStorage:", updatedUser);
                }
            }),
            catchError(error => {
                console.error("Error al actualizar el perfil:", error);
                throw error;
            })
        );
    }
}


