import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AdminServices } from '../../admin-services/admin.services';
import { NzMessageService } from 'ng-zorro-antd/message';

interface ReservationDto {
  id: number;
  roomName: string;
  roomType: string;
  checkInDate: string;
  checkOutDate: string;
  price: number;
  userName: string;
  reservationStatus: string;
}

interface ReservationResponse {
  totalPages: number;
  pageNumber: number;
  reservationDtoList: ReservationDto[];
}

@Component({
  selector: 'app-reservations.component',
  standalone: false,
  templateUrl: './reservations.component.html',
  styleUrl: './reservations.component.scss',
})
export class ReservationsComponent implements OnInit {

  currentPage = 1;
  pageSize = 4;
  total = 0;
  reservations: ReservationDto[] = [];

  constructor(
    private adminService: AdminServices,
    private message: NzMessageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.getReservations();
  }

  getReservations() {
    this.adminService.getReservations(this.currentPage - 1).subscribe({
      next: (res: ReservationResponse) => {
        this.reservations = Array.isArray(res?.reservationDtoList)
          ? res.reservationDtoList
          : [];
        this.total = (res?.totalPages ?? 0) * this.pageSize;

        if (this.reservations.length === 0 && (res?.totalPages ?? 0) > 0) {
          this.message.warning('No reservations found for this page.', {
            nzDuration: 5000,
          });
        }
        this.cdr.detectChanges();
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to load reservations right now.';

        this.message.error(errorMessage, { nzDuration: 5000 });
        this.reservations = [];
        this.total = 0;
        this.cdr.detectChanges();
      },
    });
  }

  pageIndexChange(value: number) {
    this.currentPage = value;
    this.getReservations();
  }

  changeReservationStatus(bookingId: number, status: 'APPROVED' | 'REJECTED') {
    this.adminService.changeReservationStatus(bookingId, status).subscribe({
      next: () => {
        this.message.success('Reservation status updated successfully', {
          nzDuration: 5000,
        });
        this.getReservations();
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to update reservation status right now.';

        this.message.error(errorMessage, { nzDuration: 5000 });
      },
    });
  }
}
