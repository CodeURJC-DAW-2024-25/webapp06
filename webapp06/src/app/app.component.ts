import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // Note the plural styleUrls
})
export class AppComponent {
  title = 'GlobalMart';
}