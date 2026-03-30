import { Component, OnInit } from '@angular/core';
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
export class DashboardComponent implements OnInit {
  currentPage = 1;
  totalPages = 0;
  pageSize = 1;
  rooms: RoomDto[] = [];
  hotelRoomImage =
    'https://commons.wikimedia.org/wiki/Special:Redirect/file/Hotel%20Room%20%2832259665218%29.jpg';

  constructor(
    private adminService: AdminServices,
    private message: NzMessageService
  ) {}

  ngOnInit(): void {
    setTimeout(() => this.getRooms());
  }

  getRooms() {
    this.adminService.getRooms(this.currentPage - 1).subscribe({
      next: (res: RoomsResponse) => {
        this.rooms = res.roomDtoList ?? [];
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

  pageIndexChange(value: number) {
    this.currentPage = value;
    this.getRooms();
  }
}
