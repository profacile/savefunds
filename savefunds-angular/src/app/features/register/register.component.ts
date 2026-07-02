import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'sf-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: '../login/login.component.css'
})
export class RegisterComponent {
  prenom = 'Steve';
  nom = 'Monthe';
  email = `demo${Date.now()}@savefunds.be`;
  password = 'password';
  role: 'DIRIGEANT' | 'COMPTABLE' = 'DIRIGEANT';
  showPassword = signal(false);
  loading = signal(false);
  error = signal('');

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  revealPassword(): void {
    this.showPassword.set(true);
  }

  hidePassword(): void {
    this.showPassword.set(false);
  }

  submit(): void {
    this.loading.set(true);
    this.error.set('');

    this.auth.register(this.email, this.password, this.nom, this.prenom, this.role).subscribe({
      next: () => {
        this.loading.set(false);
        void this.router.navigateByUrl('/');
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Inscription impossible. Verifiez que le backend tourne et que cet email n existe pas deja.');
      }
    });
  }
}
