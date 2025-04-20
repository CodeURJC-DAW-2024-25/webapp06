import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ApiConfigService {
    private apiBaseUrl = 'https://localhost:8443/v1/api';

    getApiBaseUrl(): string {
        return this.apiBaseUrl;
    }
}