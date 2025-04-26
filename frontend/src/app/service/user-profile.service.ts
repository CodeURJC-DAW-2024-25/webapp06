import { Injectable } from "@angular/core";
import { environment } from "../enviroments/enviroment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
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
}