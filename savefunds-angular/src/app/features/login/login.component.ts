import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'sf-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = 'demo@savefunds.be';
  password = 'password';
  loading = signal(false);
  error = signal('');

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  submit(): void {
    this.loading.set(true);
    this.error.set('');

    this.auth.login(this.email, this.password).subscribe({
      next: () => {
        this.loading.set(false);
        void this.router.navigateByUrl('/');
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Connexion impossible. Verifiez les identifiants ou creez un utilisateur via Swagger.');
      }
    });
  }
}
