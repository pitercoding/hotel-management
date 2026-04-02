import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { AdminServices } from '../../../admin/admin-services/admin.services';
import { CustomerService, RoomDto, RoomsResponse } from '../../service/customer.service';

@Component({
  selector: 'app-rooms',
  standalone: false,
  templateUrl: './rooms.component.html',
  styleUrl: './rooms.component.scss',
})
export class RoomsComponent implements OnInit {
  currentPage = 1;
  totalPages = 0;
  pageSize = 6;
  rooms: RoomDto[] = [];
  hotelRoomImage =
    'https://commons.wikimedia.org/wiki/Special:Redirect/file/Hotel%20Room%20%2832259665218%29.jpg';

  constructor(
    private customerService: CustomerService,
    private message: NzMessageService,
    private modalService: NzModalService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.getRooms();
  }

  getRooms() {
    this.customerService.getRooms(this.currentPage - 1).subscribe({
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
}
