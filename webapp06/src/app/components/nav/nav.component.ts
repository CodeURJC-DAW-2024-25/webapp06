import { Component } from '@angular/core';

@Component({
  selector: 'app-nav',
  standalone: false,
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent {
  isCompany: boolean = false;
  isAdmin: boolean = false;
  isUser: boolean = true;
  logged: boolean = true;
  username: string = 'Jack Joe';

  getHomeLink(): string {
    if (this.isCompany) {
      return '/products/allProducts';
    } else if (this.isAdmin) {
      return '/adminPage';
    } else {
      return '/home';
    }
  }
}