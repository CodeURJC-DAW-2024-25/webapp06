// src/app/components/shopping-cart/shopping-cart.component.ts
import { Component, OnInit } from '@angular/core';
import { ShoppingCartService } from '../../service/shopping-cart.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css'],
  standalone: false,
})
export class ShoppingCartComponent implements OnInit {
  cart: any = { cartProducts: [], totalPrice: 0 };
  loading = true;
  isEmpty = true;
  processingPayment = false;

  constructor(
    private shoppingCartService: ShoppingCartService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    this.shoppingCartService.getShoppingCart().subscribe(
      data => {
        this.cart = data;
        this.isEmpty = this.cart.cartProducts.length === 0;
        this.loading = false;
      },
      error => {
        console.error('Error loading cart', error);
        this.loading = false;
      }
    );
  }

  removeProduct(productId: number): void {
    this.shoppingCartService.removeProductFromCart(productId).subscribe(
      () => {
        this.loadCart();
      },
      error => {
        console.error('Error removing product', error);
      }
    );
  }

  processPayment(): void {
    this.processingPayment = true;
    this.shoppingCartService.processPayment().subscribe(
      () => {
        this.processingPayment = false;
        // Navegar a una página de confirmación o mostrar un mensaje de éxito
        this.router.navigate(['/payment-success']);
      },
      error => {
        console.error('Error processing payment', error);
        this.processingPayment = false;
      }
    );
  }
}