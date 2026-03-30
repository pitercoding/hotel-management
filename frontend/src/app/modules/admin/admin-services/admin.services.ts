import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserStorageService } from '../../../auth/services/storage/user-storage.service';

const BASIC_URL = 'http://localhost:8080/';

export interface PostRoomRequest {
  name: string;
  type: string;
  price: number;
}

@Injectable({
  providedIn: 'root',
})
export class AdminServices {
  constructor(private http: HttpClient) {}

  postRoomDetails(roomDto: PostRoomRequest): Observable<void> {
    return this.http.post<void>(BASIC_URL + 'api/admin/room', roomDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  private createAuthorizationHeader(): HttpHeaders {
    const token = UserStorageService.getToken();
    let authHeaders = new HttpHeaders();

    if (!token) {
      return authHeaders;
    }

    return authHeaders.set('Authorization', 'Bearer ' + token);
  }
}
