import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserProfileService } from '../../service/user-profile.service';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: false,
})


export class UserProfileComponent {
  user: any = null;
  profile_image: string = '';
  name: string = '';
  username: string = '';
  email: string = "";
  isGoogleUser: boolean = false;
  isUser: boolean = true;
  isCompany: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private userService: UserProfileService,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    const userId = this.authService.getUserIdByUsername(currentUser.username).toString();
    console.log(userId);
    if (userId) {
      this.loadProfileInfo(userId);
    } else {
      console.log("No se puede obtener el id del usuario");
    }

  }


  loadProfileInfo(userId: string): void {
    this.userService.getUserInfo(userId).subscribe({

      next: (data) => {
        this.user = data;  // Guarda la respuesta en 'userDTO'
        this.profile_image = data.profileImage || '';  // Asigna la imagen de perfil si está disponible
        this.name = data.name;
        this.username = data.username;
        this.email = data.email;
        this.isGoogleUser = data.isGoogleUser || false;
        this.isUser = data.isUser || false;
        this.isCompany = data.isCompany || false;
        console.log('Información del perfil cargada', this.user);
      },
      error: (err) => {
        console.error('Error al cargar el perfil', err);
      }
    });
  }

  showStadistics() {
    console.log('Show statistics clicked');
    // Implementar lógica si es necesario
  }
}