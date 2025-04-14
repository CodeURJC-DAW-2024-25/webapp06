import { Component } from '@angular/core';
import { FooterComponent } from '../footer/footer.component';
import { NavComponent } from '../nav/nav.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  standalone: true, // Cambiar a true para poder usar imports
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  imports: [NavComponent, FooterComponent, CommonModule, RouterModule],
})
export class UserProfileComponent {
  userDTO: any = null; // Cambiar a null en lugar de boolean
  profile_image: string = 'assets/images/default-user.png'; // Usa una imagen por defecto
  name: string = 'Jack Joe';
  username: string = 'jackjoe';
  email: string = "jackjoe@gmail.com";
  isGoogleUser: boolean = false;
  isUser: boolean = true;
  isCompany: boolean = false;

  showStadistics() {
    console.log('Show statistics clicked');
    // Implementar lógica si es necesario
  }
}