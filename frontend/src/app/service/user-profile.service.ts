import { Injectable } from "@angular/core";
import { environment } from "../enviroments/enviroment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { AuthService } from "./auth.service";



@Injectable({
    providedIn:'root'
})

export class UserProfileService{
    private apiUrl = `${environment.apiUrl}/users`

    constructor(private http:HttpClient){}

    getUserInfo(userId: string): Observable<any>{

        
        // const param = new HttpParams().set('id', userId);
        return this.http.get(`${this.apiUrl}/${userId}`)

    }
}