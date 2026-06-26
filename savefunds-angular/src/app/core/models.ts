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
}

export interface CreateEnterpriseRequest {
  userId: number;
  raisonSociale: string;
  numeroEntreprise: string;
  formeJuridique: string;
  secteurActivite: string;
  tresorerie: number;
  soldeCompteCourant: number;
  chiffreAffairesMensuel: number;
  chargesMensuelles: number;
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
