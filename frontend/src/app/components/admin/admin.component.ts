import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../service/product.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  imports: [CommonModule]
})
export class AdminComponent implements OnInit {
  productsNotAccepted: any[] = []; // Lista de productos no aceptados

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProductsNotAccepted();
  }

  // Cargar productos no aceptados
  loadProductsNotAccepted(): void {
    this.productService.getProductsNotAccepted().subscribe(
      (products) => {
        this.productsNotAccepted = products;
      },
      (error) => {
        console.error('Error loading products not accepted:', error);
      }
    );
  }

  // Aceptar producto
  acceptProduct(productId: number): void {
    this.productService.acceptProduct(productId).subscribe(
      () => {
        console.log(`Product ${productId} accepted successfully.`);
        this.productsNotAccepted = this.productsNotAccepted.filter(product => product.id !== productId);
      },
      (error) => {
        console.error(`Error accepting product ${productId}:`, error);
      }
    );
  }

  // Rechazar producto
  declineProduct(productId: number): void {
    this.productService.declineProduct(productId).subscribe(
      () => {
        console.log(`Product ${productId} declined successfully.`);
        this.productsNotAccepted = this.productsNotAccepted.filter(product => product.id !== productId);
      },
      (error) => {
        console.error(`Error declining product ${productId}:`, error);
      }
    );
  }
}
