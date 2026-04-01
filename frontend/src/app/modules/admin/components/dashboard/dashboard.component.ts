import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {
  AdminServices,
  RoomDto,
  RoomsResponse,
} from '../../admin-services/admin.services';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  currentPage = 1;
  totalPages = 0;
  pageSize = 6;
  rooms: RoomDto[] = [];
  hotelRoomImage =
    'https://commons.wikimedia.org/wiki/Special:Redirect/file/Hotel%20Room%20%2832259665218%29.jpg';

  constructor(
    private adminService: AdminServices,
    private message: NzMessageService,
    private modalService: NzModalService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.getRooms();
  }

  getRooms() {
    this.adminService.getRooms(this.currentPage - 1).subscribe({
      next: (res: RoomsResponse) => {
        this.rooms = [...(res.roomDtoList ?? [])];
        this.totalPages = res.totalPages;
        this.currentPage = res.pageNumber + 1;

        if (this.currentPage > this.totalPages && this.totalPages > 0) {
          this.currentPage = this.totalPages;
          this.getRooms();
          return;
        }

        this.cdr.detectChanges();
      },
      error: () => {
        this.message.error('Unable to load rooms right now.', {
          nzDuration: 5000,
        });
        this.cdr.detectChanges();
      },
    });
  }

  pageIndexChange(value: number) {
    this.currentPage = value;
    this.getRooms();
  }

  showConfirm(roomId: number) {
    this.modalService.confirm({
      nzTitle: 'Confirm',
      nzContent: 'Do you want to delete this room?',
      nzOkText: 'Delete',
      nzCancelText: 'Cancel',
      nzOnOk: () => this.deleteRoom(roomId)
    })
  }

  deleteRoom(roomId: number) {
    this.adminService.deleteRoom(roomId).subscribe({
      next: () => {
        this.message.success('Room deleted successfully', {
          nzDuration: 5000,
        });

        if (this.rooms.length === 1 && this.currentPage > 1) {
          this.currentPage -= 1;
        }

        this.getRooms();
        this.cdr.detectChanges();
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to delete the room right now.';

        this.message.error(errorMessage, { nzDuration: 5000 });
        this.cdr.detectChanges();
      },
    });
  }
}
