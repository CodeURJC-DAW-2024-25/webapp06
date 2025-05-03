import { Component } from '@angular/core';
import { Router } from '@angular/router';  // Para redirigir después de guardar los cambios
import { UserProfileService } from '../../service/user-profile.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.css'],
  standalone: false
})

export class EditProfileForm {

constructor (private Router: Router, private userProfileService: UserProfileService) {}

  user = {
    username: null,
    email: null,
    name: null
  }



  onSubmit(): void {
    this.userProfileService.editProfile(this.user).subscribe({
      next: (response) => {
          console.log("Profile updated:", response);
      },
      error: (err) => {
          console.error("Error updating profile:", err);
      },
    }); 

  }







}