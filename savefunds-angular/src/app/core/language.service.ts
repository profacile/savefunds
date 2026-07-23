import { Injectable, signal } from '@angular/core';

export type SaveFundsLanguage = 'fr' | 'en' | 'nl';

const STORAGE_KEY = 'savefunds.language';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  readonly languages: { code: SaveFundsLanguage; label: string; name: string }[] = [
    { code: 'fr', label: 'FR', name: 'Francais' },
    { code: 'en', label: 'EN', name: 'English' },
    { code: 'nl', label: 'NL', name: 'Nederlands' }
  ];

  readonly language = signal<SaveFundsLanguage>(this.initialLanguage());

  setLanguage(language: SaveFundsLanguage): void {
    this.language.set(language);
    localStorage.setItem(STORAGE_KEY, language);
    document.documentElement.lang = language;
  }

  t(key: string): string {
    return TRANSLATIONS[this.language()][key] ?? TRANSLATIONS.fr[key] ?? key;
  }

  locale(): string {
    if (this.language() === 'nl') {
      return 'nl-BE';
    }
    if (this.language() === 'en') {
      return 'en-BE';
    }
    return 'fr-BE';
  }

  private initialLanguage(): SaveFundsLanguage {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved === 'fr' || saved === 'en' || saved === 'nl') {
      document.documentElement.lang = saved;
      return saved;
    }
    document.documentElement.lang = 'fr';
    return 'fr';
  }
}

const TRANSLATIONS: Record<SaveFundsLanguage, Record<string, string>> = {
  fr: {
    financialVigilance: 'Vigilance financiere',
    myProfile: 'Mon profil',
    companyFile: 'Fiche entreprise',
    financialSources: 'Sources financieres',
    dashboard: 'Tableau de bord',
    audit: 'Audit',
    logout: 'Deconnexion',
    accountantArea: 'Espace comptable',
    belgianCompany: 'PME / SRL belge',
    director: 'Dirigeant',
    accountant: 'Comptable',
    portfolioDashboard: 'Tableau de bord portefeuille',
    accountantProfile: 'Profil comptable',
    auditPortfolio: 'Audit portefeuille',
    companyName: 'Nom de l entreprise',
    currentCash: 'Tresorerie actuelle',
    currentAccount: 'Compte courant',
    activeSources: 'Sources actives',
    noSource: 'Aucune source',
    consolidatedConfidence: 'Confiance consolidee',
    sourceActive: 'Source active',
    autoSaveFunds: 'Automatique SaveFunds',
    sourceHint: 'Les donnees et la simulation suivent la source choisie. Les sources absentes sont desactivees.',
    available: 'Disponible',
    noDataAvailable: 'Aucune donnee disponible',
    consolidatedIndicators: 'Indicateurs financiers consolides',
    hierarchyCalculation: 'Calcul selon la hierarchie SaveFunds',
    importSourceStart: 'Importez une source financiere pour commencer',
    importSourceHint: 'BNB, bilan provisoire ou extrait bancaire. Le tableau de bord calculera ensuite les indicateurs automatiquement.',
    goToFinancialSources: 'Aller aux sources financieres',
    decision: 'Decision',
    simulateWithdrawal: 'Simuler un retrait',
    decisionType: 'Type de decision',
    requestedAmount: 'Montant demande',
    analyze: 'Analyser',
    before: 'Avant',
    after: 'Apres',
    maxRecommended: 'Max recommande',
    monthsExpenses: 'mois de charges',
    debtorDays: 'jours debiteur',
    treasury: 'Tresorerie',
    monthlyRevenue: 'CA mensuel',
    expenses: 'Charges',
    directorCurrentAccount: 'CC dirigeant',
    indicator: 'Indicateur',
    value: 'Valeur',
    sourceUsed: 'Source utilisee',
    confidence: 'Confiance'
  },
  en: {
    financialVigilance: 'Financial vigilance',
    myProfile: 'My profile',
    companyFile: 'Company file',
    financialSources: 'Financial sources',
    dashboard: 'Dashboard',
    audit: 'Audit',
    logout: 'Sign out',
    accountantArea: 'Accountant area',
    belgianCompany: 'Belgian SME / SRL',
    director: 'Director',
    accountant: 'Accountant',
    portfolioDashboard: 'Portfolio dashboard',
    accountantProfile: 'Accountant profile',
    auditPortfolio: 'Portfolio audit',
    companyName: 'Company name',
    currentCash: 'Current cash',
    currentAccount: 'Current account',
    activeSources: 'Active sources',
    noSource: 'No source',
    consolidatedConfidence: 'Consolidated confidence',
    sourceActive: 'Active source',
    autoSaveFunds: 'Automatic SaveFunds',
    sourceHint: 'Data and simulations follow the selected source. Missing sources are disabled.',
    available: 'Available',
    noDataAvailable: 'No data available',
    consolidatedIndicators: 'Consolidated financial indicators',
    hierarchyCalculation: 'Calculated with the SaveFunds hierarchy',
    importSourceStart: 'Import a financial source to start',
    importSourceHint: 'NBB, provisional balance sheet, or bank statement. The dashboard will then calculate indicators automatically.',
    goToFinancialSources: 'Go to financial sources',
    decision: 'Decision',
    simulateWithdrawal: 'Simulate a withdrawal',
    decisionType: 'Decision type',
    requestedAmount: 'Requested amount',
    analyze: 'Analyze',
    before: 'Before',
    after: 'After',
    maxRecommended: 'Max recommended',
    monthsExpenses: 'months of expenses',
    debtorDays: 'debtor days',
    treasury: 'Cash',
    monthlyRevenue: 'Monthly revenue',
    expenses: 'Expenses',
    directorCurrentAccount: 'Director current account',
    indicator: 'Indicator',
    value: 'Value',
    sourceUsed: 'Source used',
    confidence: 'Confidence'
  },
  nl: {
    financialVigilance: 'Financiele waakzaamheid',
    myProfile: 'Mijn profiel',
    companyFile: 'Bedrijfsfiche',
    financialSources: 'Financiele bronnen',
    dashboard: 'Dashboard',
    audit: 'Audit',
    logout: 'Afmelden',
    accountantArea: 'Accountantruimte',
    belgianCompany: 'Belgische kmo / BV',
    director: 'Bestuurder',
    accountant: 'Accountant',
    portfolioDashboard: 'Portefeuille-dashboard',
    accountantProfile: 'Accountantsprofiel',
    auditPortfolio: 'Portefeuille-audit',
    companyName: 'Bedrijfsnaam',
    currentCash: 'Huidige cashpositie',
    currentAccount: 'Rekening-courant',
    activeSources: 'Actieve bronnen',
    noSource: 'Geen bron',
    consolidatedConfidence: 'Geconsolideerd vertrouwen',
    sourceActive: 'Actieve bron',
    autoSaveFunds: 'Automatisch SaveFunds',
    sourceHint: 'Gegevens en simulaties volgen de gekozen bron. Ontbrekende bronnen zijn uitgeschakeld.',
    available: 'Beschikbaar',
    noDataAvailable: 'Geen gegevens beschikbaar',
    consolidatedIndicators: 'Geconsolideerde financiele indicatoren',
    hierarchyCalculation: 'Berekend volgens de SaveFunds-hierarchie',
    importSourceStart: 'Importeer een financiele bron om te starten',
    importSourceHint: 'NBB, voorlopige balans of bankuittreksel. Het dashboard berekent daarna automatisch de indicatoren.',
    goToFinancialSources: 'Naar financiele bronnen',
    decision: 'Beslissing',
    simulateWithdrawal: 'Opname simuleren',
    decisionType: 'Type beslissing',
    requestedAmount: 'Gevraagd bedrag',
    analyze: 'Analyseren',
    before: 'Voor',
    after: 'Na',
    maxRecommended: 'Max. aanbevolen',
    monthsExpenses: 'maanden kosten',
    debtorDays: 'dagen debet',
    treasury: 'Cashpositie',
    monthlyRevenue: 'Maandelijkse omzet',
    expenses: 'Kosten',
    directorCurrentAccount: 'Rekening-courant bestuurder',
    indicator: 'Indicator',
    value: 'Waarde',
    sourceUsed: 'Gebruikte bron',
    confidence: 'Vertrouwen'
  }
};
