
import { Injectable } from '@angular/core';
import { environment } from '../enviroments/enviroment';

@Injectable({
    providedIn: 'root'
})
export class ApiConfigService {
    private apiBaseUrl = 'https://localhost:8443/v1/api';

    getApiBaseUrl(): string {
        return this.apiBaseUrl;
    }
}