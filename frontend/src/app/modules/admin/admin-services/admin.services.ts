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
export class AdminServices {
  constructor(private http: HttpClient) {}

  postRoomDetails(roomDto: PostRoomRequest): Observable<void> {
    return this.http.post<void>(BASIC_URL + 'api/admin/room', roomDto, {
      headers: this.createAuthorizationHeader(),
    });
  }

  getRooms(pageNumber: number): Observable<RoomsResponse> {
    return this.http.get<RoomsResponse>(BASIC_URL + `api/admin/rooms/${pageNumber}`, {
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
