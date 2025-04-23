// src/app/components/product-detail/product-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../service/product.service';
import { Cart, ShoppingCartService } from '../../service/shopping-cart.service';
import { AuthService } from '../../service/auth.service';
import { finalize } from 'rxjs';
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: ShoppingCartService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.loadProduct(+productId);
    }
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
}