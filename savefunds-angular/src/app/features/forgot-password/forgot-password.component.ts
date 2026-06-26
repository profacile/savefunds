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
  error = signal('');

  constructor(private readonly auth: AuthService) {}

  submit(): void {
    this.loading.set(true);
    this.message.set('');
    this.error.set('');

    this.auth.forgotPassword(this.email).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.message.set(response.message);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Demande impossible pour le moment. Verifiez que le backend est demarre.');
      }
    });
  }
}
