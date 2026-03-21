import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const BASIC_URL = 'http://localhost:8080/';

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
}

export interface SignupResponse {
  id?: number | string;
  message?: string;
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  register(signupRequest: SignupRequest): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(BASIC_URL + 'api/auth/signup', signupRequest);
  }
}
