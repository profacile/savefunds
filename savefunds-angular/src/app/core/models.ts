export interface User {
  id: number;
  email: string;
  lastName: string;
  firstName: string;
  role: string;
  emailVerified?: boolean;
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
  legalName: string;
  enterpriseNumber: string;
  legalForm?: string;
  activitySector?: string;
  cashBalance: number;
  directorCurrentAccountBalance?: number;
  monthlyRevenue: number;
  monthlyExpenses: number;
  status?: string;
}

export interface CreateEnterpriseRequest {
  userId: number;
  legalName: string;
  enterpriseNumber: string;
  legalForm: string;
  activitySector: string;
  cashBalance: number | null;
  directorCurrentAccountBalance: number | null;
  monthlyRevenue: number | null;
  monthlyExpenses: number | null;
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
  companyId: number;
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
  companyId: number;
  source: string;
  sourceReference: string;
  monthlyRevenue: number;
  monthlyExpenses: number;
  cashBalance: number;
  directorCurrentAccountBalance: number;
  shortTermDebt: number;
  customerReceivables: number;
  directorCurrentAccountDebtorDays: number;
  snapshotDate: string;
  confidenceScore: number;
  warnings: string[];
  missingFields: string[];
  rawMetadata?: string;
  createdAt: string;
}

export interface BankTransaction {
  id: number;
  companyId: number;
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

export interface ValidationDecision {
  id: number;
  companyId: number;
  decisionType: string;
  requestedAmount: number;
  status: 'PENDING' | 'APPROVED' | 'APPROVED_WITH_CONDITION' | 'CORRECTION_REQUESTED' | 'POSTPONED' | 'REJECTED';
  conditionText?: string;
  comment?: string;
  requestedByUserId: number;
  decidedByAccountantId?: number;
  decidedAt?: string;
  createdAt: string;
}

export interface AuditLog {
  id: number;
  companyId: number;
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

export interface AccountantClientAccess {
  id: number;
  accountantId: number;
  accountantName: string;
  accountantEmail: string;
  companyId: number;
  companyName: string;
  enterpriseNumber: string;
  status: 'PENDING' | 'ACTIVE' | 'REJECTED' | 'REVOKED';
  requestNote?: string;
  responseNote?: string;
  decidedAt?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface AccountantClientSummary {
  companyId: number;
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
