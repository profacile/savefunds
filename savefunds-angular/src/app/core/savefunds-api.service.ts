import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AccountantDashboard,
  AuditLog,
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

  createEnterprise(request: CreateEnterpriseRequest): Observable<Enterprise> {
    return this.http.post<Enterprise>(`${API_URL}/api/v1/entreprises`, request);
  }

  createMockSnapshot(enterpriseId: number, source: 'mock-bnb' | 'mock-bank' | 'mock-balance-sheet'): Observable<FinancialSnapshot> {
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

  simulateDecision(enterpriseId: number, amount: number, type = 'RETRAIT_DIRIGEANT'): Observable<VigilanceResult> {
    return this.http.post<VigilanceResult>(
      `${API_URL}/api/v1/entreprises/${enterpriseId}/financial-snapshots/simulate`,
      { type, amount }
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
