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
  isUser: boolean = true;
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