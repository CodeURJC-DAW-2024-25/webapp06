import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: false,
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  submitted = false;
  error: string = '';

  // Add the registerData object with required properties
  registerData: any = {
    name: '',
    username: '',
    email: '',
    password: '',
    role: 'USER'
  };

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {
      validator: this.passwordMatchValidator
    });
  }

  // Custom validator for password matching
  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  // Getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Handle the file upload if needed
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.registerData.imageBase64 = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    // Using registerData instead of form values
    this.authService.register(this.registerData)
      .subscribe(
        () => {
          this.router.navigate(['/login'], { queryParams: { registered: true } });
        },
        error => {
          this.error = error.error?.message || 'Registration failed';
          this.loading = false;
        }
      );
  }
}