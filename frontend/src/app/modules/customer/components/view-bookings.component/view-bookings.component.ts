import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { NzMessageService } from 'ng-zorro-antd/message';

interface BookingDto {
  id: number;
  roomName: string;
  roomType: string;
  checkInDate: string;
  checkOutDate: string;
  price: number;
  reservationStatus: string;
}

interface BookingResponse {
  totalPages: number;
  pageNumber: number;
  reservationDtoList: BookingDto[];
}

@Component({
  selector: 'app-view-bookings.component',
  standalone: false,
  templateUrl: './view-bookings.component.html',
  styleUrl: './view-bookings.component.scss',
})
export class ViewBookingsComponent implements OnInit {

  currentPage = 1;
  pageSize = 4;
  total = 0;
  bookings: BookingDto[] = [];

  constructor(
    private customerService: CustomerService,
    private message: NzMessageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.getBookings();
  }

  getBookings() {
    this.customerService.getMyBookings(this.currentPage - 1).subscribe({
      next: (res: BookingResponse) => {
        this.bookings = Array.isArray(res?.reservationDtoList)
          ? res.reservationDtoList
          : [];
        this.total = (res?.totalPages ?? 0) * this.pageSize;
        this.cdr.detectChanges();
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to load bookings right now.';

        this.message.error(errorMessage, { nzDuration: 5000 });
        this.bookings = [];
        this.total = 0;
        this.cdr.detectChanges();
      },
    });
  }

  pageIndexChange(value: number) {
    this.currentPage = value;
    this.getBookings();
  }
}
