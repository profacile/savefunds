export interface User {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  role: string;
  photoUrl?: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface MessageResponse {
  message: string;
}

export interface Enterprise {
  id: number;
  userId: number;
  raisonSociale: string;
  numeroEntreprise: string;
  formeJuridique?: string;
  secteurActivite?: string;
  tresorerie: number;
  soldeCompteCourant?: number;
  chiffreAffairesMensuel: number;
  chargesMensuelles: number;
  statut?: string;
}

export interface CreateEnterpriseRequest {
  userId: number;
  raisonSociale: string;
  numeroEntreprise: string;
  formeJuridique: string;
  secteurActivite: string;
  tresorerie: number | null;
  soldeCompteCourant: number | null;
  chiffreAffairesMensuel: number | null;
  chargesMensuelles: number | null;
}

export interface CompanyRegistryCompany {
  enterpriseNumber: string;
  name: string;
  legalForm: string;
  status: string;
  address: string;
  postalCode: string;
  city: string;
  naceCode: string;
  activityLabel: string;
  source: string;
  active: boolean;
}

export interface CompanyRegistryImportResult {
  importedRows: number;
  skippedRows: number;
  source: string;
}

export interface BnbAnnualAccountsLookup {
  id: number;
  entrepriseId: number;
  enterpriseNumber: string;
  status: 'FOUND' | 'NOT_FOUND' | 'UNAVAILABLE';
  consultUrl: string;
  xbrlUrl?: string;
  pdfUrl?: string;
  csvUrl?: string;
  latestDepositId?: string;
  latestReference?: string;
  latestModelName?: string;
  latestPeriodEndDate?: string;
  latestDepositDate?: string;
  depositsCount?: number;
  source: string;
  message: string;
  rawMetadata: string;
  createdAt: string;
}

export interface FinancialSnapshot {
  id: number;
  entrepriseId: number;
  source: string;
  sourceReference: string;
  chiffreAffairesMensuel: number;
  chargesMensuelles: number;
  tresorerie: number;
  soldeCompteCourant: number;
  dettesCourtTerme: number;
  creancesClients: number;
  dureeCompteCourantDebiteur: number;
  snapshotDate: string;
  confidenceScore: number;
  warnings: string[];
  missingFields: string[];
  rawMetadata?: string;
  createdAt: string;
}

export interface BankTransaction {
  id: number;
  entrepriseId: number;
  financialSnapshotId?: number;
  transactionDate: string;
  description: string;
  amount: number;
  balance?: number;
  classification: string;
  reviewStatus: string;
  confidenceScore: number;
  impactsDirectorCurrentAccount: boolean;
  directorCurrentAccountImpact: number;
  aiReason: string;
  createdAt: string;
}

export interface VigilanceIndicator {
  code: string;
  label: string;
  value: string;
  decision: string;
  explanation: string;
}

export interface VigilanceResult {
  snapshotId: number;
  decisionType: string;
  requestedAmount: number;
  cashBefore: number;
  cashAfter: number;
  maxRecommendedAmount: number;
  coverageMonthsAfterDecision: number;
  globalDecision: string;
  globalExplanation: string;
  recommendations: string[];
  indicators: VigilanceIndicator[];
}

export interface AuditLog {
  id: number;
  entrepriseId: number;
  userId: number;
  userEmail: string;
  action: string;
  outcome: string;
  resourceType: string;
  resourceId?: number;
  details: string;
  createdAt: string;
}

export interface AccountantDashboard {
  totalClients: number;
  greenClients: number;
  orangeClients: number;
  redClients: number;
  clients: AccountantClientSummary[];
}

export interface AccountantClientSummary {
  entrepriseId: number;
  companyName: string;
  companyNumber: string;
  status: 'VERT' | 'ORANGE' | 'ROUGE';
  dossierStatus: string;
  riskScore: number;
  cash: number;
  coverageMonths: number;
  currentAccountDebtorDays: number;
  trend: 'UP' | 'DOWN' | 'STABLE';
  dataAgeDays: number;
  nextObligationType: string;
  nextObligationDate: string;
  pendingValidationCount: number;
  pendingValidationLabel: string;
  lastSource: string;
  lastUpdate: string;
  internalNote: string;
  activity: string[];
}
