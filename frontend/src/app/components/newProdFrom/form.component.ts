import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../service/product.service';
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
  selectedFile: File | null = null; // Añadir esta propiedad para el archivo seleccionado

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
    // El código existente se mantiene igual
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

  onSubmit() {
    const token = this.productService['authService'].getToken();

    if (!token) {
      console.error('No hay token de autenticación. Inicie sesión primero.');
      alert('Su sesión ha expirado. Por favor, inicie sesión nuevamente.');
      this.router.navigate(['/login']);
      return;
    }

    // Verifica explícitamente si el usuario tiene rol COMPANY
    const user = this.productService['authService'].getCurrentUser();
    const isCompany = this.productService['authService'].hasRole('COMPANY');

    if (!isCompany) {
      console.error('Usuario no tiene permisos para crear productos. Se requiere rol COMPANY');
      alert('No tienes permisos para crear productos. Se requiere una cuenta de empresa.');
      return;
    }

    // Continuar con la creación del producto si tiene los permisos necesarios
    if (this.productForm.invalid) {
      console.error('Formulario inválido');
      return;
    }
    // Si hay una imagen, convertirla a Base64 primero
    if (this.selectedFile) {
      const reader = new FileReader();
      reader.readAsDataURL(this.selectedFile);
      reader.onload = () => {
        // Crea un objeto JSON con todos los datos, incluida la imagen en base64
        const productData = {
          name: this.productForm.value.name,
          price: this.productForm.value.price,
          description: this.productForm.value.description,
          type: this.productForm.value.type,
          stock: this.productForm.value.stock,
          imageBase64: reader.result?.toString().split(',')[1] // Quita el prefijo 'data:image/jpeg;base64,'
        };

        this.sendProduct(productData);
      };
    } else {
      // Si no hay imagen, envía los datos sin imagen
      const productData = {
        name: this.productForm.value.name,
        price: this.productForm.value.price,
        description: this.productForm.value.description,
        type: this.productForm.value.type,
        stock: this.productForm.value.stock
      };

      this.sendProduct(productData);
    }
  }

  // Método auxiliar para enviar los datos
  sendProduct(productData: any) {
    this.productService.createProduct(productData).subscribe({
      next: (response) => {
        console.log('Producto creado con éxito:', response);
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error al crear producto:', error);
      }
    });
  }

  // Manejar el cambio de imagen - Versión corregida
  onImageChange(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      const file = files[0];

      // Verificar explícitamente el tipo de archivo y logearlo
      console.log('Tipo de archivo seleccionado:', file.type);

      // Aceptar solo tipos de imagen comunes
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/jpg'];

      if (!allowedTypes.includes(file.type)) {
        console.error('Tipo de archivo no soportado:', file.type);
        alert('Solo se permiten archivos de imagen (JPG, PNG, GIF)');
        return;
      }

      // Si el tipo es válido, continuar
      this.selectedFile = file;
      // Usar el operador de acceso seguro (?) o non-null assertion (!), ya que sabemos que selectedFile no es null aquí
      console.log('Archivo seleccionado:', this.selectedFile?.name, 'Tipo:', this.selectedFile?.type);
    }
  }
}