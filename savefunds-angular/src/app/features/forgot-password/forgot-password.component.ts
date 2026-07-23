import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'sf-forgot-password',
  imports: [FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: '../login/login.component.css'
})
export class ForgotPasswordComponent {
  email = '';
  loading = signal(false);
  message = signal('');
  resetLink = signal('');
  error = signal('');

  constructor(private readonly auth: AuthService) {}

  submit(): void {
    this.loading.set(true);
    this.message.set('');
    this.resetLink.set('');
    this.error.set('');

    this.auth.forgotPassword(this.email).subscribe({
      next: (response) => {
        this.loading.set(false);
        const linkPrefix = 'Lien de reinitialisation genere: ';
        if (response.message.startsWith(linkPrefix)) {
          this.message.set('Lien de reinitialisation genere pour la demo.');
          this.resetLink.set(response.message.substring(linkPrefix.length));
          return;
        }
        this.message.set(response.message);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Demande impossible pour le moment. Verifiez que le backend est demarre.');
      }
    });
  }
}
