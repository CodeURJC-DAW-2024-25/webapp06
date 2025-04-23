import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../service/product.service'; // Servicio para interactuar con la API
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css'],
  standalone: false
})
export class FormComponent implements OnInit {
  productForm: FormGroup;
  product: any = null;
  productTypes: string[] = ['Technology', 'Books', 'Education', 'Appliances', 'Sports', 'Home', 'Music', 'Cinema'];
  form_title: string = 'Create New Product';

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      price: [0, [Validators.required, Validators.min(0)]],
      description: ['', Validators.required],
      type: ['', Validators.required],
      stock: [0, [Validators.required, Validators.min(0)]],
      image: [null],
    });
  }

  ngOnInit(): void {
    const productId = this.route.snapshot.params['id'];
    if (productId) {
      this.loadProduct(productId);
    }
  }

  // Cargar un producto desde la API
  loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe(
      (data) => {
        this.product = data;
        this.form_title = 'Edit Product';
        this.productForm.patchValue({
          name: this.product.name,
          price: this.product.price,
          description: this.product.description,
          type: this.product.type,
          stock: this.product.stock,
        });
      },
      (error) => {
        console.error('Error loading product:', error);
      }
    );
  }

  // Manejar el envío del formulario
  onSubmit(): void {
    if (this.productForm.valid) {
        const formData = this.productForm.value;

        if (this.product) {
            // Actualizar producto existente
            this.productService.updateProduct(this.product.id, formData).subscribe(
                () => {
                    console.log('Product updated successfully');
                    this.router.navigate(['/products']); // Redirige después de actualizar
                },
                (error) => {
                    console.error('Error updating product:', error);
                }
            );
        } else {
            // Crear un nuevo producto
            this.productService.createProduct(formData).subscribe(
                () => {
                    console.log('Product created successfully');
                    this.router.navigate(['/products']); // Redirige después de crear
                },
                (error) => {
                    console.error('Error creating product:', error);
                }
            );
        }
    } else {
        console.error('Form is invalid');
    }
  }

  // Manejar el cambio de imagen
  onImageChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.productForm.patchValue({ image: reader.result });
      };
      reader.readAsDataURL(file);
    }
  }
}