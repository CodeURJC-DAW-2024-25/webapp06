import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../footer/footer.component';
import { NavComponent } from '../nav/nav.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, FormsModule, NavComponent, FooterComponent],
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private router: Router) { }

  onSubmit(): void {
    console.log('Login attempt with username:', this.username);

    // Aquí puedes agregar la lógica de autenticación
    // Por ejemplo, llamar a un servicio de autenticación

    // Ejemplo básico de navegación después de inicio de sesión
    if (this.username && this.password) {
      // Si la autenticación es exitosa, redirige al usuario
      this.router.navigate(['/home']);
    } else {
      // Manejo de error si es necesario
      console.error('Username and password are required');
    }
  }
}