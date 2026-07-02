import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AccountantDashboard,
  AuditLog,
  BankTransaction,
  BnbAnnualAccountsLookup,
  CompanyRegistryCompany,
  CompanyRegistryImportResult,
  CreateEnterpriseRequest,
  Enterprise,
  FinancialSnapshot,
  User,
  VigilanceResult
} from './models';

const API_URL = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class SaveFundsApiService {
  constructor(private readonly http: HttpClient) {}

  getEnterpriseByUser(userId: number): Observable<Enterprise> {
    return this.http.get<Enterprise>(`${API_URL}/api/v1/entreprises/user/${userId}`);
  }

  getMyEnterprises(): Observable<Enterprise[]> {
    return this.http.get<Enterprise[]>(`${API_URL}/api/v1/entreprises/me`);
  }

  createEnterprise(request: CreateEnterpriseRequest): Observable<Enterprise> {
    return this.http.post<Enterprise>(`${API_URL}/api/v1/entreprises`, request);
  }

  updateEnterprise(enterpriseId: number, request: Partial<CreateEnterpriseRequest>): Observable<Enterprise> {
    return this.http.put<Enterprise>(`${API_URL}/api/v1/entreprises/${enterpriseId}`, request);
  }

  deleteEnterprise(enterpriseId: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/api/v1/entreprises/${enterpriseId}`);
  }

  searchCompanyRegistry(query: string): Observable<CompanyRegistryCompany[]> {
    return this.http.get<CompanyRegistryCompany[]>(`${API_URL}/api/v1/company-registry/search`, {
      params: { query }
    });
  }

  findCompanyRegistryByNumber(enterpriseNumber: string): Observable<CompanyRegistryCompany> {
    return this.http.get<CompanyRegistryCompany>(`${API_URL}/api/v1/company-registry/${encodeURIComponent(enterpriseNumber)}`);
  }

  importCompanyRegistry(file: File): Observable<CompanyRegistryImportResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<CompanyRegistryImportResult>(`${API_URL}/api/v1/company-registry/import`, formData);
  }

  createEnterpriseFromRegistry(company: CompanyRegistryCompany): Observable<Enterprise> {
    return this.http.post<Enterprise>(`${API_URL}/api/v1/entreprises/from-registry`, {
      enterpriseNumber: company.enterpriseNumber,
      name: company.name,
      legalForm: company.legalForm,
      status: company.status,
      address: company.address,
      postalCode: company.postalCode,
      city: company.city,
      naceCode: company.naceCode,
      source: company.source,
      active: company.active,
      activityLabel: company.activityLabel
    });
  }

  searchBnbAnnualAccounts(enterpriseId: number): Observable<BnbAnnualAccountsLookup> {
    return this.http.post<BnbAnnualAccountsLookup>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/bnb/annual-accounts/search`,
      {}
    );
  }

  getLatestBnbAnnualAccounts(enterpriseId: number): Observable<BnbAnnualAccountsLookup> {
    return this.http.get<BnbAnnualAccountsLookup>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/bnb/annual-accounts/latest`
    );
  }

  importLatestBnbAnnualAccounts(enterpriseId: number): Observable<FinancialSnapshot> {
    return this.http.post<FinancialSnapshot>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/bnb/annual-accounts/import-latest`,
      {}
    );
  }

  createMockSnapshot(enterpriseId: number, source: 'mock-bank' | 'mock-balance-sheet'): Observable<FinancialSnapshot> {
    return this.http.post<FinancialSnapshot>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/${source}`,
      {}
    );
  }

  getLatestSnapshot(enterpriseId: number): Observable<FinancialSnapshot> {
    return this.http.get<FinancialSnapshot>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/latest`
    );
  }

  getFinancialSnapshots(enterpriseId: number): Observable<FinancialSnapshot[]> {
    return this.http.get<FinancialSnapshot[]>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots`
    );
  }

  getConsolidatedSnapshot(enterpriseId: number): Observable<FinancialSnapshot> {
    return this.http.get<FinancialSnapshot>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/consolidated`
    );
  }

  importBankCsv(enterpriseId: number, file: File): Observable<FinancialSnapshot> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FinancialSnapshot>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/import-bank-csv`,
      formData
    );
  }

  importAccountingCsv(enterpriseId: number, file: File): Observable<FinancialSnapshot> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FinancialSnapshot>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/import-accounting-csv`,
      formData
    );
  }

  getBankTransactions(enterpriseId: number): Observable<BankTransaction[]> {
    return this.http.get<BankTransaction[]>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/bank-transactions`
    );
  }

  simulateDecision(enterpriseId: number, amount: number, type = 'RETRAIT_DIRIGEANT', forcedSource?: string): Observable<VigilanceResult> {
    return this.http.post<VigilanceResult>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/simulate`,
      { type, amount, forcedSource: forcedSource || null }
    );
  }

  getAuditLogs(enterpriseId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${API_URL}/api/v1/entreprises/${enterpriseId}/audit-logs`);
  }

  getAccountantDashboard(): Observable<AccountantDashboard> {
    return this.http.get<AccountantDashboard>(`${API_URL}/api/v1/accountants/me/dashboard`);
  }

  updateUser(userId: number, request: Partial<User>): Observable<User> {
    return this.http.put<User>(`${API_URL}/api/v1/users/${userId}`, request);
  }
}
