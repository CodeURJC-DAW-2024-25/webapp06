import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css'],
  standalone: false
})
export class FormComponent {
  productForm: FormGroup;
  product: any = null; // Representa el producto actual (null si se está creando uno nuevo)
  productTypes: string[] = ['Technology', 'Books', 'Education', 'Appliances', 'Sports', 'Home', 'Music', 'Cinema'];
  form_title: string = 'Create New Product'; // Título dinámico del formulario

  constructor(private fb: FormBuilder) {
    // Inicializa el formulario con valores predeterminados
    this.productForm = this.fb.group({
      name: ['', [Validators.required ]],
      price: [0, [Validators.required, Validators.min(0)]],
      description: ['', Validators.required],
      type: ['', Validators.required],
      stock: [0, [Validators.required, Validators.min(0)]],
      image: [null] // Para manejar la imagen del producto
    });

    // Simula la carga de un producto existente (puedes reemplazar esto con datos reales)
    this.loadProduct();
  }

  ngOnInit(): void {

  }
  // Método para cargar un producto existente (simulación)
  loadProduct(): void {
    // Simula un producto cargado desde un servicio o API
    this.product = {
      id: 1,
      name: 'Sample Product',
      price: 99.99,
      description: 'This is a sample product description.',
      type: 'Technology',
      stock: 10,
      imageBase64: 'data:image/png;base64,...' // Imagen en base64
    };

    // Si hay un producto, actualiza el formulario y el título
    if (this.product) {
      this.form_title = 'Edit Product';
      this.productForm.patchValue({
        name: this.product.name,
        price: this.product.price,
        description: this.product.description,
        type: this.product.type,
        stock: this.product.stock
      });
    }
  }

}