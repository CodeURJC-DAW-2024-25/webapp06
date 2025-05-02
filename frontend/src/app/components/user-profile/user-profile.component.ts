import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserProfileService } from '../../service/user-profile.service';
import { AuthService } from '../../service/auth/auth.service';
import { map, switchMap, switchScan } from 'rxjs';

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
  isUser: boolean = false;
  isCompany: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private userService: UserProfileService,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    let userId = null
    const currentUser = this.authService.getCurrentUser();
    console.log("Usuario" + currentUser.username)
    console.log("Rol del usuario:  " + currentUser.role)

    if (currentUser.role == "COMPANY"){
      this.isCompany = true
      console.log("Rol de compañia ")
    } else if (currentUser.role == "USER"){
      this.isUser = true
      console.log("Rol de usaurio")
    } else {
      console.log("Otro rol" + currentUser.roles)
    }
    
    this.authService.getUserIdByUsername(currentUser.username).subscribe({
      next: (userIdObject) => {
        userId = userIdObject.toString()
        this.loadProfileInfo(userId);
      },
      error: (err) => {
        console.error('Error al cargar el perfil', err);
      }
    }

    )
  }


  loadProfileInfo(userId: string): void {
    this.userService.getUserInfo(userId).subscribe({
        next: (data) => {
            this.user = data;

            // Cargar la imagen del usuario
            this.userService.getUserImage(userId).subscribe({
                next: (imageData) => {
                    console.log('Imagen cargada:', imageData);
                    this.profile_image = imageData;
                },
                error: (err) => {
                    console.error('Error al cargar la imagen del usuario', err);
                }
            });
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