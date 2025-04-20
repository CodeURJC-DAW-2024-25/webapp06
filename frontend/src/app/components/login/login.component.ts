import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false,
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string = '/';
  error: string = '';

  // Add these properties to fix the errors
  username: string = '';
  password: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    // Obtain the return URL from query parameters or use the home page
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  // Getter to easily access form fields
  get f() { return this.loginForm.controls; }

  onSubmit(): void {
    this.submitted = true;

    // Stop here if the form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    // Use the ngModel values instead of form values
    this.authService.login(this.username, this.password)
      .subscribe(
        () => {
          this.router.navigate([this.returnUrl]);
        },
        error => {
          this.error = error.error?.message || 'Invalid username or password';
          this.loading = false;
        }
      );
  }
}