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

export interface UpdateRoomRequest {
  name: string;
  type: string;
  price: number;
}

export interface RoomDto {
  id: number;
  name: string;
  type: string;
  price: number;
  available: boolean;
}

export interface RoomsResponse {
  roomDtoList: RoomDto[];
  totalPages: number;
  pageNumber: number;
}

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  constructor(private http: HttpClient) {}

  getRooms(pageNumber: number): Observable<RoomsResponse> {
    return this.http.get<RoomsResponse>(BASIC_URL + `api/customer/rooms/${pageNumber}`, {
      headers: this.createAuthorizationHeader(),
    });
  }

  bookRoom(bookingDto: any): Observable<any> {
    return this.http.post(BASIC_URL + `api/customer/book`, bookingDto, {
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
