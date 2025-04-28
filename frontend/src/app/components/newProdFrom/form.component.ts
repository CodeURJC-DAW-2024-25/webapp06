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
  selectedFile: File | null = null; // Variable para almacenar el archivo de imagen seleccionado

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
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { product: any };
  
    if (state?.product) {
      this.product = state.product;
    } else {
      this.product = this.productService.getProduct();
    }
  
    if (this.product) {
      this.form_title = 'Edit Product';
      this.productForm.patchValue({
        name: this.product.name,
        price: this.product.price,
        description: this.product.description,
        type: this.product.type,
        stock: this.product.stock,
      });
    } else {
      this.form_title = 'Create New Product';
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

                    // Si hay una imagen seleccionada, actualizar la imagen
                    if (this.selectedFile) {
                        const imageFormData = new FormData();
                        imageFormData.append('file', this.selectedFile); // Adjuntar el archivo seleccionado
                        
                        this.productService.updateProductImage(this.product.id, imageFormData).subscribe(
                            () => {
                                console.log('Product image updated successfully');
                                this.router.navigate(['/products']); // Redirigir después de actualizar
                            },
                            (error) => {
                                console.error('Error updating product image:', error);
                            }
                        );
                    } else {
                        this.router.navigate(['/products']); // Redirigir si no hay imagen
                    }
                },
                (error) => {
                    console.error('Error updating product:', error);
                }
            );
        } else {
            // Crear un nuevo producto
            this.productService.createProduct(formData).subscribe(
                (createdProduct) => {
                    console.log('Product created successfully');

                    // Si hay una imagen seleccionada, subir la imagen
                    if (this.selectedFile) {
                        const imageFormData = new FormData();
                        imageFormData.append('file', this.selectedFile); // Adjuntar el archivo seleccionado

                        this.productService.updateProductImage(createdProduct.id, imageFormData).subscribe(
                            () => {
                                console.log('Product image uploaded successfully');
                                this.router.navigate(['/products']); // Redirigir después de crear
                            },
                            (error) => {
                                console.error('Error uploading product image:', error);
                            }
                        );
                    } else {
                        this.router.navigate(['/products']); // Redirigir si no hay imagen
                    }
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
        this.selectedFile = file; // Almacenar el archivo seleccionado
        console.log('Imagen seleccionada:', file.name);
    }
  }
}