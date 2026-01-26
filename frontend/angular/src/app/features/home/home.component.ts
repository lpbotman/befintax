import {Component, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {
  MatCard,
  MatCardActions,
  MatCardAvatar,
  MatCardContent,
  MatCardHeader,
  MatCardTitle
} from '@angular/material/card';
import {RouterLink} from '@angular/router';
import {AuthService} from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  imports: [
    MatButton,
    MatIcon,
    MatCardActions,
    MatCardContent,
    MatCardAvatar,
    MatCardTitle,
    MatCardHeader,
    MatCard,
    RouterLink
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  auth = inject(AuthService);

  scrollToFeatures() {
    document.getElementById('vision-section')?.scrollIntoView({ behavior: 'smooth' });
  }

}
