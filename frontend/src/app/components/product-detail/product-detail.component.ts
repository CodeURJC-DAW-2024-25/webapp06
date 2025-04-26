// src/app/components/product-detail/product-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../service/product.service';
import { Cart, ShoppingCartService } from '../../service/shopping-cart.service';
import { AuthService } from '../../service/auth/auth.service';
import { finalize } from 'rxjs';
import { NavComponent } from '../nav/nav.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';


@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css'],
  standalone: false,
})
export class ProductDetailComponent implements OnInit {
  product: any = null;
  loading = true;
  addingToCart = false;
  showSuccessMessage = false;  // Añadir esta propiedad
  showErrorMessage = false;    // Añadir esta propiedad
  isAdmin = false; // Variable para almacenar si el usuario es administrador
  reviewForm: FormGroup; // Formulario para las reseñas
  isUser = false; // Verificar si el usuario tiene el rol USER

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: ShoppingCartService,
    private authService: AuthService,
    private fb: FormBuilder // Inyectar FormBuilder
) {
    // Inicializar el formulario de reseñas
    this.reviewForm = this.fb.group({
        rating: [null, [Validators.required, Validators.min(1), Validators.max(5)]],
        comment: ['', [Validators.required, Validators.maxLength(500)]],
    });
}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
        this.loadProduct(+productId);
    }

    // Suscribirse al observable user$ para obtener la información del usuario
    this.authService.user$.subscribe((user) => {
        if (user) {
            this.isUser = Array.isArray(user.roles)
                ? user.roles.includes('USER')
                : user.roles === 'USER';
        } else {
            this.isUser = false;
        }
    });
    this.authService.user$.subscribe((user) => {
      if (user) {
          this.isAdmin = Array.isArray(user.roles)
              ? user.roles.includes('ADMIN')
              : user.roles === 'ADMIN';
      } else {
          this.isAdmin = false;
      }
  });
}

  loadProduct(id: number): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe(
      data => {
        this.product = data;
        this.loading = false;
      },
      error => {
        console.error('Error loading product', error);
        this.loading = false;
        this.router.navigate(['/products/allProducts']);
      }
    );
  }

  addToCart(): void {
    if (!this.authService.getCurrentUser()) {
      // Si el usuario no está autenticado, guardamos la URL actual para redireccionar después del login
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.addingToCart = true;

    // Usar el nuevo método que combina añadir al carrito y redirigir
    this.cartService.addToCartAndNavigate(this.product.id, 1);

    // La gestión del estado addingToCart se puede hacer con un timeout simple
    // ya que la redirección ocurrirá rápidamente
    setTimeout(() => {
      this.addingToCart = false;
    }, 500);
  }

  editProduct(): void {
    if (this.product) {
      this.productService.setProduct(this.product);
      this.router.navigate(['/newProduct']);
    } else {
      console.error('No product loaded to edit.');
    }
  }

  submitReview(): void {
    if (this.reviewForm.valid) {
        const reviewData = this.reviewForm.value;
        console.log('Submitting review:', reviewData);

        // Llamar al servicio para enviar la reseña
        this.productService.addReview(this.product.id, reviewData).subscribe(
            (response) => {
                console.log('Review submitted successfully:', response);
                // Actualizar las reseñas del producto
                this.product.reviews.push(response);
                this.reviewForm.reset();
            },
            (error) => {
                console.error('Error submitting review:', error);
            }
        );
    }
}

}