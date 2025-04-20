// src/app/components/product-detail/product-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../service/product.service';
import { ShoppingCartService } from '../../service/shopping-cart.service';
import { AuthService } from '../../service/auth.service';
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
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.addingToCart = true;
    this.cartService.addProductToCart(this.product.id).subscribe(
      () => {
        this.addingToCart = false;
        // Mostrar mensaje de éxito
      },
      error => {
        console.error('Error adding to cart', error);
        this.addingToCart = false;
      }
    );
  }
}