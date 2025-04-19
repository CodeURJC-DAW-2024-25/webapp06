import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
  standalone: false,
})
export class ProductListComponent implements OnInit {
  // Propiedades ya existentes
  type: string | null = null;
  products: any[] = [
    { id: 1, name: 'Product 1', image: 'assets/images/product1.jpg', price: 99.99, category: 'Electronics' },
    { id: 2, name: 'Product 2', image: 'assets/images/product2.jpg', price: 149.99, category: 'Clothing' },
    { id: 3, name: 'Product 3', image: 'assets/images/product3.jpg', price: 199.99, category: 'Books' },
    { id: 4, name: 'Product 4', image: 'assets/images/product4.jpg', price: 79.99, category: 'Electronics' },
  ];
  filteredProducts: any[] = [];

  // Nuevas propiedades requeridas
  hasMoreProducts: boolean = true;
  loading: boolean = false;
  page: number = 1;
  pageSize: number = 10;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    // Captura el parámetro de la URL para determinar el tipo de productos a mostrar
    this.route.params.subscribe(params => {
      this.type = params['type'] || null;
      this.filterProducts();
    });
  }

  filterProducts(): void {
    if (this.type && this.type !== 'allProducts') {
      this.filteredProducts = this.products.filter(product =>
        product.category === this.type
      );
    } else {
      this.filteredProducts = [...this.products];
    }

    // Simular si hay más productos disponibles
    this.hasMoreProducts = this.filteredProducts.length >= this.pageSize;
  }

  // Método para cargar más productos (simulado)
  loadMore(): void {
    this.loading = true;

    // Simular una carga asíncrona
    setTimeout(() => {
      // Aquí implementarías la lógica real para cargar más productos
      // Por ejemplo, llamar a un servicio para obtener la siguiente página

      // Incrementar la página
      this.page++;

      // Agregar productos simulados
      const moreProducts = [
        { id: 5, name: 'Product 5', image: 'assets/images/product1.jpg', price: 129.99, category: 'Electronics' },
        { id: 6, name: 'Product 6', image: 'assets/images/product2.jpg', price: 89.99, category: 'Clothing' },
      ];

      // Filtrar por categoría si es necesario
      if (this.type && this.type !== 'allProducts') {
        const filteredMoreProducts = moreProducts.filter(product => product.category === this.type);
        this.filteredProducts = [...this.filteredProducts, ...filteredMoreProducts];
      } else {
        this.filteredProducts = [...this.filteredProducts, ...moreProducts];
      }

      // Simular que no hay más productos después de cargar estos
      this.hasMoreProducts = false;
      this.loading = false;
    }, 1000); // Simular un retraso de red de 1 segundo
  }
}