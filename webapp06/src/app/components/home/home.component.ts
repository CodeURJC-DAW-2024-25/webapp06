import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SliderComponent } from '../slider/slider.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, SliderComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
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