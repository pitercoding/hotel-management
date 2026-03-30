import { Component } from '@angular/core';
import {
  AdminServices,
  RoomDto,
  RoomsResponse,
} from '../../admin-services/admin.services';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {

  currentPage = 1;
  totalPages = 0;
  rooms: RoomDto[] = [];

  constructor(
    private adminService: AdminServices,
    private message: NzMessageService
  ) {
    this.getRooms();
  }

  getRooms() {
    this.adminService.getRooms(this.currentPage - 1).subscribe({
      next: (res: RoomsResponse) => {
        this.rooms = res.roomDtoList;
        this.totalPages = res.totalPages;
        this.currentPage = res.pageNumber + 1;
      },
      error: () => {
        this.message.error('Unable to load rooms right now.', {
          nzDuration: 5000,
        });
      },
    });
  }
}
