import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'sf-reset-password',
  imports: [FormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrl: '../login/login.component.css'
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';
  showPassword = signal(false);
  loading = signal(false);
  message = signal('');
  error = signal('');

  constructor(private readonly auth: AuthService, private readonly route: ActivatedRoute) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.error.set('Lien de reinitialisation invalide ou incomplet.');
    }
  }

  revealPassword(): void {
    this.showPassword.set(true);
  }

  hidePassword(): void {
    this.showPassword.set(false);
  }

  submit(): void {
    this.loading.set(true);
    this.message.set('');
    this.error.set('');

    this.auth.resetPassword(this.token, this.newPassword, this.confirmPassword).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.message.set(response.message);
        this.newPassword = '';
        this.confirmPassword = '';
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Reinitialisation impossible. Le lien est peut-etre expire ou les mots de passe ne correspondent pas.');
      }
    });
  }
}
