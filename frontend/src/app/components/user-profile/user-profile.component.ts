import { Component } from '@angular/core';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: false,
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