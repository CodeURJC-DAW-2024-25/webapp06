import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css'],
  standalone: true,
  imports: [RouterModule, CommonModule]
})
export class NavComponent {
  isCompany: boolean = false;
  isAdmin: boolean = false;
  isUser: boolean = true;
  logged: boolean = false;
  username: string = 'Jack Joe';

  getHomeLink(): string {
    if (this.isCompany) {
      return '/products/allProducts';
    } else if (this.isAdmin) {
      return '/adminPage';
    } else {
      return '/redir';
    }
  }
}