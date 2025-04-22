import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: false
})
export class RegisterComponent {
  registerForm: FormGroup;
  error: string = '';
  loading: boolean = false;
  selectedFile: File | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      role: ['USER', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    const { username, email, password, role } = this.registerForm.value;

    // Imprime los datos que se envían
    console.log('Enviando datos:', { username, email, password, roles: [role] });

    this.authService.register(username, email, password, role).subscribe({
      next: (response) => {
        console.log('Respuesta exitosa:', response);
        // Si necesitas subir un archivo después del registro, lo puedes hacer aquí
        if (this.selectedFile) {
          // Implementa la lógica para subir la imagen al perfil del usuario
        }
        this.router.navigate(['/login'], { queryParams: { registered: true } });
      },
      error: (err) => {
        console.error('Error durante el registro:', err);

        // Si es un error 200, probablemente sea un problema de formato de respuesta
        if (err.status === 200) {
          // Intentar registrar al usuario de todos modos, ya que el backend respondió con éxito
          this.router.navigate(['/login'], { queryParams: { registered: true } });
        } else {
          this.error = err.error?.message || 'An error occurred during registration';
        }

        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}