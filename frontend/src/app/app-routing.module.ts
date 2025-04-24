import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { ShoppingCartComponent } from './components/shopping-cart/shopping-cart.component';
import { AuthGuard } from './guard/auth.guard';
import { ProductListComponent } from './components/product-list/product-list.component';
import { AboutUsComponent } from './components/about-us/about-us.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { FormComponent } from './components/newProdFrom/form.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { UserGraphComponent } from './components/user-graph/user-graph.component';


export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'about-us', component: AboutUsComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'products/category/:category', component: ProductListComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'newProduct', component: FormComponent},
  { path: 'user-profile', component: UserProfileComponent},
  { path: 'user-graph', component: UserGraphComponent},

  // Rutas protegidas
  { path: 'shoppingcart', component: ShoppingCartComponent, canActivate: [AuthGuard] },

  // Redirigir a la página principal para cualquier ruta desconocida
  { path: '**', redirectTo: '' }
];