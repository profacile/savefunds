import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { SaveFundsApiService } from '../../core/savefunds-api.service';
import {
  AccountantClientSummary,
  AuditLog,
  CreateEnterpriseRequest,
  Enterprise,
  FinancialSnapshot,
  VigilanceResult
} from '../../core/models';

type MockSource = 'mock-bnb' | 'mock-bank' | 'mock-balance-sheet';
type DashboardView = 'PORTFOLIO' | 'PROFILE' | 'INGESTION' | 'SIMULATION' | 'AUDIT';

interface AccountantClient {
  name: string;
  companyNumber: string;
  status: 'VERT' | 'ORANGE' | 'ROUGE';
  dossierStatus: string;
  riskScore: number;
  cash: number;
  coverageMonths: number;
  currentAccountDays: number;
  trend: 'UP' | 'DOWN' | 'STABLE';
  dataAgeDays: number;
  nextObligation: string;
  nextObligationDate: string;
  validationCount: number;
  pendingValidation: string;
  lastSource: string;
  lastUpdate: string;
  internalNote: string;
  activity: string[];
}

@Component({
  selector: 'sf-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  enterprise = signal<Enterprise | null>(null);
  snapshot = signal<FinancialSnapshot | null>(null);
  result = signal<VigilanceResult | null>(null);
  auditLogs = signal<AuditLog[]>([]);
  loading = signal('');
  error = signal('');
  selectedClient = signal<AccountantClient | null>(null);
  accountantFilter = signal<'ALL' | 'ALERTS' | 'VALIDATIONS' | 'DUE_SOON' | 'STALE_DATA'>('ALL');
  currentView = signal<DashboardView>('PORTFOLIO');
  profilePhoto = signal<string | null>(this.readProfilePhoto());

  withdrawalAmount = 3000;
  decisionType = 'RETRAIT_DIRIGEANT';

  accountantClients = signal<AccountantClient[]>([
    {
      name: 'Atelier Verhaegen SRL',
      companyNumber: 'BE 0734.221.908',
      status: 'ROUGE',
      dossierStatus: 'Action comptable requise',
      riskScore: 8.7,
      cash: 8200,
      coverageMonths: 0.7,
      currentAccountDays: 48,
      trend: 'DOWN',
      dataAgeDays: 1,
      nextObligation: 'TVA',
      nextObligationDate: '2026-07-20',
      validationCount: 2,
      pendingValidation: 'Retrait dirigeant 4 000 EUR',
      lastSource: 'Banque PSD2 mock',
      lastUpdate: 'Aujourd hui',
      internalNote: 'Attendre encaissement facture client avant tout retrait.',
      activity: ['26/06 - Demande retrait 4 000 EUR', '25/06 - Import bancaire mock', '21/06 - Alerte CC debiteur']
    },
    {
      name: 'Studio Delta SRL',
      companyNumber: 'BE 0791.554.120',
      status: 'ORANGE',
      dossierStatus: 'Analyse a revoir',
      riskScore: 5.9,
      cash: 18450,
      coverageMonths: 1.4,
      currentAccountDays: 21,
      trend: 'DOWN',
      dataAgeDays: 9,
      nextObligation: 'ONSS',
      nextObligationDate: '2026-07-05',
      validationCount: 1,
      pendingValidation: 'Paiement fournisseur 6 500 EUR',
      lastSource: 'Bilan PDF mock',
      lastUpdate: 'Hier',
      internalNote: 'Verifier creances clients ouvertes avant paiement fournisseur.',
      activity: ['25/06 - Parsing bilan mock', '24/06 - Simulation paiement fournisseur', '18/06 - Note comptable ajoutee']
    },
    {
      name: 'Profacile SRL',
      companyNumber: 'BE 0123.456.789',
      status: 'VERT',
      dossierStatus: 'A jour',
      riskScore: 2.1,
      cash: 42400,
      coverageMonths: 3.1,
      currentAccountDays: 0,
      trend: 'UP',
      dataAgeDays: 2,
      nextObligation: 'Precompte',
      nextObligationDate: '2026-07-15',
      validationCount: 0,
      pendingValidation: 'Aucune',
      lastSource: 'BNB mock',
      lastUpdate: 'Il y a 2 jours',
      internalNote: 'Dossier stable. Revoir apres declaration TVA.',
      activity: ['24/06 - Snapshot BNB mock', '22/06 - Analyse verte', '15/06 - TVA preparee']
    },
    {
      name: 'MecaNord SRL',
      companyNumber: 'BE 0668.441.330',
      status: 'ORANGE',
      dossierStatus: 'En attente client',
      riskScore: 6.4,
      cash: 15100,
      coverageMonths: 1.1,
      currentAccountDays: 36,
      trend: 'STABLE',
      dataAgeDays: 42,
      nextObligation: 'TVA',
      nextObligationDate: '2026-07-20',
      validationCount: 1,
      pendingValidation: 'Depense exceptionnelle 2 800 EUR',
      lastSource: 'CSV comptable',
      lastUpdate: 'Il y a 42 jours',
      internalNote: 'Relancer le dirigeant pour un extrait bancaire recent.',
      activity: ['15/05 - CSV comptable importe', '16/05 - Alerte donnees anciennes', '20/05 - Relance client']
    }
  ]);

  filteredAccountantClients = computed(() => {
    const filter = this.accountantFilter();
    return this.accountantClients().filter((client) => {
      if (filter === 'ALERTS') {
        return client.status !== 'VERT';
      }
      if (filter === 'VALIDATIONS') {
        return client.validationCount > 0;
      }
      if (filter === 'DUE_SOON') {
        return this.daysUntil(client.nextObligationDate) <= 14;
      }
      if (filter === 'STALE_DATA') {
        return client.dataAgeDays > 30;
      }
      return true;
    }).sort((a, b) => b.riskScore - a.riskScore);
  });

  accountantStats = computed(() => ({
    total: this.accountantClients().length,
    green: this.accountantClients().filter((client) => client.status === 'VERT').length,
    orange: this.accountantClients().filter((client) => client.status === 'ORANGE').length,
    red: this.accountantClients().filter((client) => client.status === 'ROUGE').length
  }));

  isAccountant = computed(() => this.auth.currentUser()?.role === 'COMPTABLE');

  enterpriseForm: CreateEnterpriseRequest = {
    userId: 0,
    raisonSociale: 'Profacile SRL',
    numeroEntreprise: 'BE 0123.456.789',
    formeJuridique: 'SRL',
    secteurActivite: 'Services informatiques',
    tresorerie: 25000,
    soldeCompteCourant: -2500,
    chiffreAffairesMensuel: 42000,
    chargesMensuelles: 31000
  };

  coverage = computed(() => {
    const current = this.snapshot();
    if (!current || !current.chargesMensuelles) {
      return 0;
    }
    return current.tresorerie / current.chargesMensuelles;
  });

  constructor(
    readonly auth: AuthService,
    private readonly api: SaveFundsApiService
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user) {
      return;
    }
    if (user.role === 'COMPTABLE') {
      this.selectedClient.set(this.accountantClients()[0]);
      this.loadAccountantDashboard();
      return;
    }
    this.enterpriseForm.userId = user.id;
    this.loadEnterprise(user.id);
  }

  selectClient(client: AccountantClient): void {
    this.selectedClient.set(client);
  }

  setView(view: DashboardView): void {
    this.currentView.set(view);
  }

  onProfilePhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const photo = String(reader.result);
      localStorage.setItem(this.profilePhotoKey(), photo);
      this.profilePhoto.set(photo);
      const user = this.auth.currentUser();
      if (user) {
        this.api.updateUser(user.id, { photoUrl: photo }).subscribe({
          next: (updated) => this.auth.updateCurrentUser(updated),
          error: () => undefined
        });
      }
    };
    reader.readAsDataURL(file);
  }

  removeProfilePhoto(): void {
    localStorage.removeItem(this.profilePhotoKey());
    this.profilePhoto.set(null);
    const user = this.auth.currentUser();
    if (user) {
      this.api.updateUser(user.id, { photoUrl: '' }).subscribe({
        next: (updated) => this.auth.updateCurrentUser(updated),
        error: () => undefined
      });
    }
  }

  setAccountantFilter(filter: 'ALL' | 'ALERTS' | 'VALIDATIONS' | 'DUE_SOON' | 'STALE_DATA'): void {
    this.accountantFilter.set(filter);
    this.selectedClient.set(this.filteredAccountantClients()[0] ?? null);
  }

  createEnterprise(): void {
    const user = this.auth.currentUser();
    if (!user) {
      return;
    }

    this.setLoading('Creation entreprise...');
    this.api.createEnterprise({ ...this.enterpriseForm, userId: user.id }).subscribe({
      next: (enterprise) => {
        this.enterprise.set(enterprise);
        this.clearLoading();
        this.refreshLatest();
      },
      error: () => this.fail('Entreprise non creee. Elle existe peut-etre deja pour cet utilisateur.')
    });
  }

  ingest(source: MockSource): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.setLoading(this.sourceLabel(source) + '...');
    this.api.createMockSnapshot(enterprise.id, source).subscribe({
      next: (snapshot) => {
        this.snapshot.set(snapshot);
        this.result.set(null);
        this.clearLoading();
        this.refreshAudit();
      },
      error: () => this.fail('Import mock impossible. Verifiez que le backend est demarre et que le token JWT est valide.')
    });
  }

  simulate(): void {
    const enterprise = this.enterprise();
    if (!enterprise || !this.snapshot()) {
      return;
    }

    this.setLoading('Simulation...');
    this.api.simulateDecision(enterprise.id, this.withdrawalAmount, this.decisionType).subscribe({
      next: (result) => {
        this.result.set(result);
        this.clearLoading();
        this.refreshAudit();
      },
      error: () => this.fail('Simulation impossible. Creez d abord un snapshot financier.')
    });
  }

  refreshLatest(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.api.getLatestSnapshot(enterprise.id).subscribe({
      next: (snapshot) => this.snapshot.set(snapshot),
      error: () => this.snapshot.set(null)
    });
  }

  refreshAudit(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.api.getAuditLogs(enterprise.id).subscribe({
      next: (logs) => this.auditLogs.set(logs),
      error: () => this.auditLogs.set([])
    });
  }

  private loadAccountantDashboard(): void {
    this.api.getAccountantDashboard().subscribe({
      next: (dashboard) => {
        const clients = dashboard.clients.map((client) => this.toAccountantClient(client));
        this.accountantClients.set(clients);
        this.selectedClient.set(this.filteredAccountantClients()[0] ?? null);
      },
      error: () => {
        this.selectedClient.set(this.filteredAccountantClients()[0] ?? null);
      }
    });
  }

  money(value?: number | null): string {
    return new Intl.NumberFormat('fr-BE', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0
    }).format(value ?? 0);
  }

  sourceLabel(source: MockSource | string): string {
    const labels: Record<string, string> = {
      'mock-bnb': 'Import BNB mock',
      'mock-bank': 'Connexion banque mock',
      'mock-balance-sheet': 'Parsing bilan mock',
      BNB_API: 'BNB',
      BANK_API: 'Banque',
      BALANCE_SHEET_DOCUMENT: 'Bilan',
      MANUAL: 'Manuel',
      BANK_CSV: 'CSV bancaire',
      ACCOUNTING_CSV: 'CSV comptable'
    };
    return labels[source] ?? source;
  }

  private toAccountantClient(client: AccountantClientSummary): AccountantClient {
    return {
      name: client.companyName,
      companyNumber: client.companyNumber,
      status: client.status,
      dossierStatus: client.dossierStatus,
      riskScore: Number(client.riskScore ?? 0),
      cash: Number(client.cash ?? 0),
      coverageMonths: Number(client.coverageMonths ?? 0),
      currentAccountDays: client.currentAccountDebtorDays ?? 0,
      trend: client.trend,
      dataAgeDays: client.dataAgeDays ?? 999,
      nextObligation: client.nextObligationType,
      nextObligationDate: client.nextObligationDate,
      validationCount: client.pendingValidationCount ?? 0,
      pendingValidation: client.pendingValidationLabel,
      lastSource: client.lastSource,
      lastUpdate: client.lastUpdate ? new Date(client.lastUpdate).toLocaleDateString('fr-BE') : 'Inconnu',
      internalNote: client.internalNote,
      activity: client.activity ?? []
    };
  }

  trendLabel(trend?: 'UP' | 'DOWN' | 'STABLE'): string {
    if (trend === 'UP') {
      return 'Hausse';
    }
    if (trend === 'DOWN') {
      return 'Baisse';
    }
    return 'Stable';
  }

  roleLabel(): string {
    return this.isAccountant() ? 'Comptable' : 'Dirigeant';
  }

  userInitials(): string {
    const user = this.auth.currentUser();
    return `${user?.prenom?.charAt(0) ?? ''}${user?.nom?.charAt(0) ?? ''}`.trim() || 'U';
  }

  daysUntil(date: string): number {
    const today = new Date();
    const target = new Date(date + 'T00:00:00');
    return Math.ceil((target.getTime() - today.getTime()) / 86_400_000);
  }

  private loadEnterprise(userId: number): void {
    this.setLoading('Chargement...');
    this.api.getEnterpriseByUser(userId).subscribe({
      next: (enterprise) => {
        this.enterprise.set(enterprise);
        this.clearLoading();
        this.refreshLatest();
        this.refreshAudit();
      },
      error: () => {
        this.clearLoading();
        this.enterprise.set(null);
      }
    });
  }

  private setLoading(message: string): void {
    this.error.set('');
    this.loading.set(message);
  }

  private clearLoading(): void {
    this.loading.set('');
  }

  private fail(message: string): void {
    this.loading.set('');
    this.error.set(message);
  }

  private profilePhotoKey(): string {
    return `savefunds.profile-photo.${this.auth.currentUser()?.id ?? 'anonymous'}`;
  }

  private readProfilePhoto(): string | null {
    const rawUser = localStorage.getItem('savefunds.user');
    if (!rawUser) {
      return null;
    }
    try {
      const user = JSON.parse(rawUser) as { id?: number };
      return (user as { photoUrl?: string }).photoUrl || localStorage.getItem(`savefunds.profile-photo.${user.id ?? 'anonymous'}`);
    } catch {
      return null;
    }
  }
}
