import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ProductService } from '../../service/product.service';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-product-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  standalone: false
})
export class ProductSearchComponent implements OnInit {
  searchForm: FormGroup;
  products: any[] = [];
  loading = false;
  categories: string[] = ['All', 'Electronics', 'Clothing', 'Books', 'Home']; // Adjust based on your categories

  constructor(
    private fb: FormBuilder,
    private productService: ProductService
  ) {
    this.searchForm = this.fb.group({
      query: [''],
      category: ['All'],
      minPrice: [''],
      maxPrice: ['']
    });
  }

  ngOnInit(): void {
    // Reactive search as user types with debounce
    this.searchForm.get('query')?.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => this.searchProducts());

    // Initial search to load products
    this.searchProducts();
  }

  searchProducts(): void {
    this.loading = true;
    const formValues = this.searchForm.value;

    // Only pass category if it's not 'All'
    const category = formValues.category !== 'All' ? formValues.category : undefined;

    this.productService.searchProducts(
      formValues.query,
      category,
      formValues.minPrice || undefined,
      formValues.maxPrice || undefined
    ).subscribe({
      next: (results) => {
        this.products = results;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error in product search:', error);
        this.loading = false;
      }
    });
  }
}