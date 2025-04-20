import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: false,
})
export class HomeComponent {
  mostViewedProducts = [
    { id: 1, name: 'Product 1', image: 'assets/images/product1.jpg', price: 99.99 },
    { id: 2, name: 'Product 2', image: 'assets/images/product2.jpg', price: 149.99 },
    { id: 3, name: 'Product 3', image: 'assets/images/product3.jpg', price: 199.99 },
    { id: 4, name: 'Product 4', image: 'assets/images/product4.jpg', price: 79.99 },
  ];

  lastProducts = [
    { id: 1, name: 'Product 1', image: 'assets/images/product1.jpg', price: 99.99 },
    { id: 2, name: 'Product 2', image: 'assets/images/product2.jpg', price: 149.99 },
    { id: 3, name: 'Product 3', image: 'assets/images/product3.jpg', price: 199.99 },
    { id: 4, name: 'Product 4', image: 'assets/images/product4.jpg', price: 79.99 },
  ];
}