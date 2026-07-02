import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { SaveFundsApiService } from '../../core/savefunds-api.service';
import {
  AccountantClientSummary,
  AuditLog,
  BankTransaction,
  BnbAnnualAccountsLookup,
  CompanyRegistryCompany,
  CreateEnterpriseRequest,
  Enterprise,
  FinancialSnapshot,
  VigilanceResult
} from '../../core/models';

type MockSource = 'mock-bank' | 'mock-balance-sheet';
type DashboardView = 'PROFILE' | 'DETAIL' | 'SOURCES' | 'DASHBOARD' | 'AUDIT';
type SourceTab = 'BNB' | 'BALANCE' | 'BANK';

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
  enterprises = signal<Enterprise[]>([]);
  enterprise = signal<Enterprise | null>(null);
  snapshot = signal<FinancialSnapshot | null>(null);
  snapshots = signal<FinancialSnapshot[]>([]);
  bnbLookup = signal<BnbAnnualAccountsLookup | null>(null);
  result = signal<VigilanceResult | null>(null);
  auditLogs = signal<AuditLog[]>([]);
  bankTransactions = signal<BankTransaction[]>([]);
  loading = signal('');
  error = signal('');
  selectedClient = signal<AccountantClient | null>(null);
  accountantFilter = signal<'ALL' | 'ALERTS' | 'VALIDATIONS' | 'DUE_SOON' | 'STALE_DATA'>('ALL');
  currentView = signal<DashboardView>('PROFILE');
  sourceTab = signal<SourceTab>('BNB');
  selectedSource = signal<string>('AUTO');
  showAddEnterprise = signal(false);
  profilePhoto = signal<string | null>(this.readProfilePhoto());
  registryQuery = signal('');
  registryResults = signal<CompanyRegistryCompany[]>([]);
  selectedRegistryCompany = signal<CompanyRegistryCompany | null>(null);
  registrySearchMessage = signal('');
  registryAutocompleteLoading = signal(false);
  bceImportMessage = signal('');
  editingEnterpriseId = signal<number | null>(null);
  pendingEnterpriseLogo = signal<string | null>(null);
  enterpriseLogoVersion = signal(0);
  private registryAutocompleteTimer: ReturnType<typeof setTimeout> | null = null;

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

  visibleAuditLogs = computed(() => this.auditLogs()
    .filter((log) => log.action !== 'AUDIT_LOG_VIEWED')
  );

  sourceIndicators = computed(() => {
    const selected = this.selectedSource();
    const displayed = this.displayedSnapshot();
    const snapshots = selected === 'AUTO' ? this.snapshots() : (displayed ? [displayed] : []);
    return {
      tresorerie: this.sourceForField(snapshots, 'tresorerie', ['BANK_CSV', 'ACCOUNTING_CSV', 'BALANCE_SHEET_DOCUMENT', 'BNB_API']),
      revenue: this.sourceForField(snapshots, 'chiffreAffairesMensuel', ['ACCOUNTING_CSV', 'BALANCE_SHEET_DOCUMENT', 'BNB_API']),
      expenses: this.sourceForField(snapshots, 'chargesMensuelles', ['ACCOUNTING_CSV', 'BALANCE_SHEET_DOCUMENT', 'BNB_API']),
      currentAccount: this.sourceForField(snapshots, 'soldeCompteCourant', ['BANK_CSV', 'ACCOUNTING_CSV', 'BALANCE_SHEET_DOCUMENT', 'BNB_API'])
    };
  });

  activeSourceSummary = computed(() => {
    const sources = new Set(Object.values(this.sourceIndicators())
      .filter((item) => item.available)
      .map((item) => item.label));
    return sources.size ? Array.from(sources).join(' + ') : 'Aucune source';
  });

  displayedSnapshot = computed(() => {
    const selectedSource = this.selectedSource();
    if (selectedSource === 'AUTO') {
      return this.snapshot();
    }
    return this.snapshots().find((candidate) => candidate.source === selectedSource) ?? null;
  });

  availableSources = computed(() => {
    const sources = new Set(this.snapshots().map((item) => item.source));
    return [
      { value: 'AUTO', label: 'Automatique SaveFunds', available: this.snapshots().length > 0 },
      { value: 'BANK_CSV', label: 'CSV bancaire', available: sources.has('BANK_CSV') },
      { value: 'ACCOUNTING_CSV', label: 'Bilan provisoire', available: sources.has('ACCOUNTING_CSV') },
      { value: 'BNB_API', label: 'BNB officielle', available: sources.has('BNB_API') },
      { value: 'BALANCE_SHEET_DOCUMENT', label: 'Bilan document', available: sources.has('BALANCE_SHEET_DOCUMENT') }
    ];
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
    raisonSociale: '',
    numeroEntreprise: '',
    formeJuridique: '',
    secteurActivite: '',
    tresorerie: null,
    soldeCompteCourant: null,
    chiffreAffairesMensuel: null,
    chargesMensuelles: null
  };

  enterpriseEditForm: CreateEnterpriseRequest = {
    userId: 0,
    raisonSociale: '',
    numeroEntreprise: '',
    formeJuridique: '',
    secteurActivite: '',
    tresorerie: null,
    soldeCompteCourant: null,
    chiffreAffairesMensuel: null,
    chargesMensuelles: null
  };

  coverage = computed(() => {
    const current = this.displayedSnapshot();
    if (!current || !current.chargesMensuelles) {
      return 0;
    }
    return current.tresorerie / current.chargesMensuelles;
  });

  enterpriseDecision = computed(() => {
    const current = this.displayedSnapshot();
    if (!current) {
      return 'ORANGE';
    }
    const coverage = current.chargesMensuelles ? current.tresorerie / current.chargesMensuelles : 0;
    const ccDays = current.dureeCompteCourantDebiteur ?? 0;
    if (coverage < 1 || ccDays > 30) {
      return 'ROUGE';
    }
    if (coverage < 3 || ccDays > 0) {
      return 'ORANGE';
    }
    return 'VERT';
  });

  enterpriseCardDecision(enterprise: Enterprise): 'VERT' | 'ORANGE' | 'ROUGE' {
    if (this.enterprise()?.id === enterprise.id && this.displayedSnapshot()) {
      return this.enterpriseDecision() as 'VERT' | 'ORANGE' | 'ROUGE';
    }

    if (!enterprise.tresorerie || !enterprise.chargesMensuelles) {
      return 'ORANGE';
    }

    const coverage = enterprise.tresorerie / enterprise.chargesMensuelles;
    const currentAccount = enterprise.soldeCompteCourant ?? 0;
    if (coverage < 1) {
      return 'ROUGE';
    }
    if (coverage < 3 || currentAccount < 0) {
      return 'ORANGE';
    }
    return 'VERT';
  }

  enterpriseCardStatus(enterprise: Enterprise): string {
    if (this.enterprise()?.id === enterprise.id && this.displayedSnapshot()) {
      return this.enterpriseDecision();
    }
    if (!enterprise.tresorerie || !enterprise.chargesMensuelles) {
      return 'A VERIFIER';
    }
    return this.enterpriseCardDecision(enterprise);
  }

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
    this.loadEnterprises();
  }

  selectClient(client: AccountantClient): void {
    this.selectedClient.set(client);
  }

  setView(view: DashboardView): void {
    this.currentView.set(view);
  }

  openAddEnterprise(): void {
    this.showAddEnterprise.set(true);
    this.editingEnterpriseId.set(null);
    this.registryResults.set([]);
    this.registrySearchMessage.set('');
    this.selectedRegistryCompany.set(null);
  }

  startEditEnterprise(event: Event, enterprise: Enterprise): void {
    event.stopPropagation();
    this.showAddEnterprise.set(false);
    this.enterprise.set(enterprise);
    this.editingEnterpriseId.set(enterprise.id);
    this.enterpriseEditForm = {
      userId: enterprise.userId,
      raisonSociale: enterprise.raisonSociale,
      numeroEntreprise: enterprise.numeroEntreprise,
      formeJuridique: enterprise.formeJuridique || '',
      secteurActivite: enterprise.secteurActivite || '',
      tresorerie: enterprise.tresorerie ?? null,
      soldeCompteCourant: enterprise.soldeCompteCourant ?? null,
      chiffreAffairesMensuel: enterprise.chiffreAffairesMensuel ?? null,
      chargesMensuelles: enterprise.chargesMensuelles ?? null
    };
  }

  onPendingEnterpriseLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    const reader = new FileReader();
    reader.onload = () => this.pendingEnterpriseLogo.set(String(reader.result));
    reader.readAsDataURL(file);
  }

  onEnterpriseLogoSelected(event: Event, enterprise: Enterprise): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      localStorage.setItem(this.enterpriseLogoKey(enterprise.id), String(reader.result));
      this.enterpriseLogoVersion.update((value) => value + 1);
    };
    reader.readAsDataURL(file);
  }

  removeEnterpriseLogo(enterprise: Enterprise): void {
    localStorage.removeItem(this.enterpriseLogoKey(enterprise.id));
    this.enterpriseLogoVersion.update((value) => value + 1);
  }

  cancelEditEnterprise(): void {
    this.editingEnterpriseId.set(null);
  }

  updateEnterprise(): void {
    const id = this.editingEnterpriseId();
    if (!id) {
      return;
    }

    this.setLoading('Mise a jour entreprise...');
    this.api.updateEnterprise(id, this.enterpriseEditForm).subscribe({
      next: (updated) => {
        this.enterprises.update((items) => items.map((item) => item.id === updated.id ? updated : item));
        if (this.enterprise()?.id === updated.id) {
          this.enterprise.set(updated);
        }
        this.editingEnterpriseId.set(null);
        this.clearLoading();
      },
      error: () => this.fail('Mise a jour impossible. Verifiez les champs et vos droits sur cette entreprise.')
    });
  }

  deleteEnterprise(event: Event, enterprise: Enterprise): void {
    event.stopPropagation();
    const confirmed = window.confirm(`Supprimer ${this.displayEnterpriseName(enterprise)} ? Cette action supprimera aussi les donnees associees.`);
    if (!confirmed) {
      return;
    }

    this.setLoading('Suppression entreprise...');
    this.api.deleteEnterprise(enterprise.id).subscribe({
      next: () => {
        this.enterprises.update((items) => items.filter((item) => item.id !== enterprise.id));
        if (this.enterprise()?.id === enterprise.id) {
          const nextEnterprise = this.enterprises()[0] ?? null;
          this.enterprise.set(nextEnterprise);
          this.snapshot.set(null);
          this.snapshots.set([]);
          this.result.set(null);
          this.bnbLookup.set(null);
          this.bankTransactions.set([]);
          this.auditLogs.set([]);
          if (nextEnterprise) {
            this.selectEnterprise(nextEnterprise);
            this.currentView.set('PROFILE');
          }
        }
        if (this.editingEnterpriseId() === enterprise.id) {
          this.editingEnterpriseId.set(null);
        }
        this.clearLoading();
      },
      error: () => this.fail('Suppression impossible. Verifiez vos droits ou les donnees rattachees.')
    });
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

  onRegistryQueryChange(value: string): void {
    this.registryQuery.set(value);
    this.selectedRegistryCompany.set(null);

    const query = value.trim();
    if (this.registryAutocompleteTimer) {
      clearTimeout(this.registryAutocompleteTimer);
    }

    if (query.length < 3) {
      this.registryResults.set([]);
      this.registrySearchMessage.set('');
      this.registryAutocompleteLoading.set(false);
      return;
    }

    this.registryAutocompleteLoading.set(true);
    const delay = query.replace(/\D/g, '').length === 10 ? 120 : 450;
    this.registryAutocompleteTimer = setTimeout(() => this.searchRegistry(true), delay);
  }

  searchRegistry(autocomplete = false): void {
    const query = this.registryQuery().trim();
    if (query.length < 2) {
      this.registryResults.set([]);
      this.registryAutocompleteLoading.set(false);
      return;
    }

    if (!autocomplete) {
      this.setLoading('Recherche BCE...');
    }
    this.registrySearchMessage.set('');
    if (query.replace(/\D/g, '').length === 10) {
      this.api.findCompanyRegistryByNumber(query).subscribe({
        next: (company) => {
          this.registryResults.set([company]);
          this.selectedRegistryCompany.set(company);
          this.registrySearchMessage.set('1 resultat trouve via le numero BCE.');
          this.registryAutocompleteLoading.set(false);
          this.clearLoading();
        },
        error: () => {
          this.registryResults.set([]);
          this.selectedRegistryCompany.set(null);
          this.registrySearchMessage.set('Aucun resultat trouve pour ce numero BCE.');
          this.registryAutocompleteLoading.set(false);
          this.clearLoading();
        }
      });
      return;
    }

    this.api.searchCompanyRegistry(query).subscribe({
      next: (results) => {
        this.registryResults.set(results);
        this.selectedRegistryCompany.set(results[0] ?? null);
        this.registrySearchMessage.set(results.length
          ? `${results.length} suggestion(s) BCE trouvee(s) ${results[0]?.source ? '(' + results[0].source + ')' : ''}.`
          : 'Aucun resultat dans la base BCE locale ni via le fallback Public Search. Verifiez le numero BCE ou importez un extrait BCE Open Data plus complet.'
        );
        this.registryAutocompleteLoading.set(false);
        this.clearLoading();
      },
      error: () => {
        this.registrySearchMessage.set('');
        this.registryAutocompleteLoading.set(false);
        this.fail('Recherche BCE indisponible. Redemarrez le backend pour charger le fallback Public Search, puis reessayez.')
      }
    });
  }

  selectRegistryCompany(company: CompanyRegistryCompany): void {
    this.selectedRegistryCompany.set(company);
  }

  selectEnterprise(enterprise: Enterprise): void {
    this.enterprise.set(enterprise);
    this.currentView.set('DETAIL');
    this.showAddEnterprise.set(false);
    this.selectedSource.set('AUTO');
    this.snapshot.set(null);
    this.result.set(null);
    this.bnbLookup.set(null);
    this.bankTransactions.set([]);
    this.auditLogs.set([]);
    this.refreshLatest();
    this.refreshBnbLookup();
    this.refreshBankTransactions();
    this.refreshAudit();
  }

  createEnterpriseFromRegistry(): void {
    const company = this.selectedRegistryCompany();
    if (!company || !company.active) {
      this.fail('Selectionnez une entreprise active selon la BCE.');
      return;
    }

    this.setLoading('Creation depuis la BCE...');
    this.api.createEnterpriseFromRegistry(company).subscribe({
      next: (enterprise) => {
        this.enterprises.update((items) => [enterprise, ...items.filter((item) => item.id !== enterprise.id)]);
        this.savePendingEnterpriseLogo(enterprise);
        this.selectEnterprise(enterprise);
        this.enterpriseForm = {
          ...this.enterpriseForm,
          userId: enterprise.userId,
          raisonSociale: enterprise.raisonSociale,
          numeroEntreprise: enterprise.numeroEntreprise,
          formeJuridique: enterprise.formeJuridique || '',
          secteurActivite: enterprise.secteurActivite || ''
        };
        this.clearLoading();
        this.currentView.set('DETAIL');
      },
      error: (error) => this.fail(this.apiErrorMessage(error, 'Creation depuis la BCE impossible. Verifiez que cette entreprise n est pas deja rattachee a votre compte.'))
    });
  }

  onBceOpenDataSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.setLoading('Import BCE Open Data...');
    this.api.importCompanyRegistry(file).subscribe({
      next: (result) => {
        this.bceImportMessage.set(`${result.importedRows} entreprise(s) importee(s), ${result.skippedRows} ligne(s) ignoree(s).`);
        this.clearLoading();
      },
      error: () => this.fail('Import BCE impossible. Verifiez le format CSV et votre role utilisateur.')
    });
  }

  createEnterprise(): void {
    const user = this.auth.currentUser();
    if (!user) {
      return;
    }

    this.setLoading('Creation entreprise...');
    this.api.createEnterprise({ ...this.enterpriseForm, userId: user.id }).subscribe({
      next: (enterprise) => {
        this.enterprises.update((items) => [enterprise, ...items.filter((item) => item.id !== enterprise.id)]);
        this.savePendingEnterpriseLogo(enterprise);
        this.selectEnterprise(enterprise);
        this.clearLoading();
      },
      error: () => this.fail('Entreprise non creee. Elle est peut-etre deja rattachee a cet utilisateur.')
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

  searchBnb(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.setLoading('Recherche BNB comptes annuels...');
    this.api.searchBnbAnnualAccounts(enterprise.id).subscribe({
      next: (lookup) => {
        this.bnbLookup.set(lookup);
        this.clearLoading();
      },
      error: () => this.fail('Recherche BNB indisponible. Verifiez que le backend a acces a consult.cbso.nbb.be.')
    });
  }

  importBnbSnapshot(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.setLoading('Import des chiffres BNB...');
    this.api.importLatestBnbAnnualAccounts(enterprise.id).subscribe({
      next: (snapshot) => {
        this.snapshot.set(snapshot);
        this.result.set(null);
        this.clearLoading();
        this.refreshBnbLookup();
        this.refreshLatest();
        this.refreshAudit();
      },
      error: () => this.fail('Import BNB impossible. Verifiez qu un depot BNB avec CSV est disponible.')
    });
  }

  onBankCsvSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    const enterprise = this.enterprise();
    if (!file || !enterprise) {
      return;
    }

    this.setLoading('Import bancaire et classification...');
    this.api.importBankCsv(enterprise.id, file).subscribe({
      next: (snapshot) => {
        this.snapshot.set(snapshot);
        this.result.set(null);
        this.clearLoading();
        this.refreshLatest();
        this.refreshBankTransactions();
        this.refreshAudit();
      },
      error: (error) => this.fail(this.apiErrorMessage(error, 'Import bancaire impossible. Format CSV attendu pour le parseur actuel: date,description,amount,balance.'))
    });
  }

  onAccountingCsvSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    const enterprise = this.enterprise();
    if (!file || !enterprise) {
      return;
    }

    this.setLoading('Import bilan provisoire comptable...');
    this.api.importAccountingCsv(enterprise.id, file).subscribe({
      next: (snapshot) => {
        this.snapshot.set(snapshot);
        this.result.set(null);
        this.clearLoading();
        this.refreshLatest();
        this.refreshAudit();
      },
      error: (error) => this.fail(this.apiErrorMessage(error, 'Import bilan impossible. Format CSV attendu pour le parseur actuel: accountCode,label,amount.'))
    });
  }

  simulate(): void {
    const enterprise = this.enterprise();
    if (!enterprise || !this.snapshot()) {
      return;
    }

    this.setLoading('Simulation...');
    const forcedSource = this.selectedSource() === 'AUTO' ? undefined : this.selectedSource();
    this.api.simulateDecision(enterprise.id, this.withdrawalAmount, this.decisionType, forcedSource).subscribe({
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

    this.api.getConsolidatedSnapshot(enterprise.id).subscribe({
      next: (snapshot) => this.snapshot.set(snapshot),
      error: () => this.snapshot.set(null)
    });
    this.refreshSources();
  }

  refreshSources(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.api.getFinancialSnapshots(enterprise.id).subscribe({
      next: (snapshots) => this.snapshots.set(snapshots),
      error: () => this.snapshots.set([])
    });
  }

  selectFinancialSource(source: string): void {
    if (source !== 'AUTO' && !this.snapshots().some((item) => item.source === source)) {
      return;
    }
    this.selectedSource.set(source);
    this.result.set(null);
  }

  hasSource(source: string): boolean {
    return this.snapshots().some((item) => item.source === source);
  }

  latestSource(source: string): FinancialSnapshot | null {
    return this.snapshots().find((item) => item.source === source) ?? null;
  }

  sourceStatusLabel(source: string): string {
    const snapshot = this.latestSource(source);
    if (!snapshot) {
      return 'Aucune donnee';
    }
    return `${this.sourceLabel(snapshot.source)} - ${new Date(snapshot.snapshotDate).toLocaleDateString('fr-BE')}`;
  }

  displayEnterpriseName(enterprise?: Enterprise | null): string {
    const name = enterprise?.raisonSociale?.trim() || '';
    if (!name || this.isInvalidEnterpriseName(name)) {
      return 'Nom de l entreprise a corriger';
    }
    return name;
  }

  registryCompanySubtitle(company: CompanyRegistryCompany): string {
    return company.legalForm?.trim()
      ? `${company.enterpriseNumber} - ${company.legalForm}`
      : company.enterpriseNumber;
  }

  registryCompanyAddress(company: CompanyRegistryCompany): string {
    const address = [company.address, [company.postalCode, company.city].filter(Boolean).join(' ')].filter(Boolean).join(', ');
    return address || 'Adresse non renseignee';
  }

  registryCompanyActivity(company: CompanyRegistryCompany): string {
    return [company.naceCode, company.activityLabel].filter(Boolean).join(' - ') || 'Activite non renseignee';
  }

  enterpriseLogo(enterprise: Enterprise): string | null {
    this.enterpriseLogoVersion();
    return localStorage.getItem(this.enterpriseLogoKey(enterprise.id));
  }

  enterpriseInitial(enterprise: Enterprise): string {
    const name = this.displayEnterpriseName(enterprise);
    if (name === 'Nom de l entreprise a corriger') {
      return 'E';
    }
    return name.charAt(0).toUpperCase();
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

  refreshBnbLookup(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.api.getLatestBnbAnnualAccounts(enterprise.id).subscribe({
      next: (lookup) => this.bnbLookup.set(lookup),
      error: () => this.bnbLookup.set(null)
    });
  }

  refreshBankTransactions(): void {
    const enterprise = this.enterprise();
    if (!enterprise) {
      return;
    }

    this.api.getBankTransactions(enterprise.id).subscribe({
      next: (transactions) => this.bankTransactions.set(transactions),
      error: () => this.bankTransactions.set([])
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
      'mock-bank': 'Connexion banque mock',
      'mock-balance-sheet': 'Parsing bilan mock',
      BNB_API: 'BNB comptes annuels',
      BANK_API: 'Banque',
      BALANCE_SHEET_DOCUMENT: 'Bilan provisoire',
      MANUAL: 'Manuel',
      BANK_CSV: 'CSV bancaire',
      ACCOUNTING_CSV: 'Bilan provisoire comptable'
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

  viewTitle(): string {
    if (this.isAccountant()) {
      if (this.currentView() === 'AUDIT') {
        return 'Audit portefeuille';
      }
      if (this.currentView() === 'PROFILE') {
        return 'Profil comptable';
      }
      return 'Dashboard portefeuille';
    }
    const labels: Record<DashboardView, string> = {
      DETAIL: this.displayEnterpriseName(this.enterprise()) || 'Fiche entreprise',
      SOURCES: 'Sources financieres',
      DASHBOARD: 'Dashboard portefeuille',
      AUDIT: 'Audit et tracabilite',
      PROFILE: 'Mon profil'
    };
    return labels[this.currentView()];
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

  private loadEnterprises(): void {
    this.setLoading('Chargement...');
    this.api.getMyEnterprises().subscribe({
      next: (enterprises) => {
        this.enterprises.set(enterprises);
        const active = enterprises[0] ?? null;
        this.enterprise.set(active);
        this.clearLoading();
        if (active) {
          this.refreshLatest();
          this.refreshBnbLookup();
          this.refreshBankTransactions();
          this.refreshAudit();
        }
      },
      error: () => {
        this.clearLoading();
        this.enterprises.set([]);
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

  private apiErrorMessage(error: unknown, fallback: string): string {
    const payload = (error as { error?: unknown })?.error;
    if (typeof payload === 'string') {
      return payload;
    }
    if (payload && typeof payload === 'object' && 'message' in payload) {
      return String((payload as { message?: unknown }).message || fallback);
    }
    return fallback;
  }

  private sourceForField(
    snapshots: FinancialSnapshot[],
    field: keyof Pick<FinancialSnapshot, 'tresorerie' | 'chiffreAffairesMensuel' | 'chargesMensuelles' | 'soldeCompteCourant'>,
    orderedSources: string[]
  ): { available: boolean; label: string; date: string; confidence: number; warning: boolean } {
    for (const source of orderedSources) {
      const match = snapshots.find((candidate) => candidate.source === source && candidate[field] !== null && candidate[field] !== undefined);
      if (match) {
        return {
          available: true,
          label: this.sourceLabel(match.source),
          date: match.snapshotDate ? new Date(match.snapshotDate).toLocaleDateString('fr-BE') : '',
          confidence: match.confidenceScore ?? 0,
          warning: match.source === 'BNB_API'
        };
      }
    }
    return { available: false, label: 'Aucune donnée disponible', date: '', confidence: 0, warning: false };
  }

  private profilePhotoKey(): string {
    return `savefunds.profile-photo.${this.auth.currentUser()?.id ?? 'anonymous'}`;
  }

  private enterpriseLogoKey(enterpriseId: number): string {
    return `savefunds.enterprise-logo.${this.auth.currentUser()?.id ?? 'anonymous'}.${enterpriseId}`;
  }

  private savePendingEnterpriseLogo(enterprise: Enterprise): void {
    const logo = this.pendingEnterpriseLogo();
    if (!logo) {
      return;
    }
    localStorage.setItem(this.enterpriseLogoKey(enterprise.id), logo);
    this.pendingEnterpriseLogo.set(null);
    this.enterpriseLogoVersion.update((value) => value + 1);
  }

  private isInvalidEnterpriseName(name: string): boolean {
    const normalized = name.toLowerCase();
    return normalized.startsWith('ondernemingsnummer')
      || normalized.startsWith('numero d')
      || normalized.startsWith('numéro d')
      || normalized === this.enterprise()?.numeroEntreprise?.toLowerCase();
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
