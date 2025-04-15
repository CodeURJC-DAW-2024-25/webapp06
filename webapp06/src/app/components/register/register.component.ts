import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface RegisterData {
  name: string;
  username: string;
  email: string;
  password: string;
  role: string;
  image?: File;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  registerData: RegisterData = {
    name: '',
    username: '',
    email: '',
    password: '',
    role: 'USER'
  };

  constructor(private router: Router) { }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.registerData.image = event.target.files[0];
    }
  }

  onSubmit() {
    console.log('Registration submitted:', this.registerData);

    // Aquí crearías un FormData para enviar al backend
    const formData = new FormData();
    formData.append('name', this.registerData.name);
    formData.append('username', this.registerData.username);
    formData.append('mail', this.registerData.email);
    formData.append('password', this.registerData.password);
    formData.append('role', this.registerData.role);

    if (this.registerData.image) {
      formData.append('image', this.registerData.image);
    }

    // Ejemplo: Aquí implementarías la llamada al servicio de registro
    // this.authService.register(formData).subscribe(response => {
    //   this.router.navigate(['/login']);
    // });

    // Por ahora, solo navegamos al login
    this.router.navigate(['/login']);
  }
}