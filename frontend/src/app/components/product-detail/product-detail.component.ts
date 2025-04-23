// src/app/components/product-detail/product-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../service/product.service';
import { Cart, ShoppingCartService } from '../../service/shopping-cart.service';
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
    this.cartService.addToCart(this.product.id).subscribe(
      (cart: Cart) => {
        console.log('Producto añadido al carrito');
        this.showSuccessMessage = true;
      },
      (error: any) => {
        console.error('Error al añadir al carrito:', error);
        this.showErrorMessage = true;
      }
    );
  }
}