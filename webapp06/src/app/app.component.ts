import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FooterComponent } from './footer/footer.component';
import { NavComponent } from './nav/nav.component';
import { CommonModule } from '@angular/common';
import { SliderComponent } from './slider/slider.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css',
  imports: [FooterComponent, NavComponent, RouterModule, CommonModule, SliderComponent]
})
export class AppComponent {
  title = 'GlobalMart';
}