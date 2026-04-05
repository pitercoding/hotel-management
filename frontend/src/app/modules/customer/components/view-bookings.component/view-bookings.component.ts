import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-view-bookings.component',
  imports: [],
  templateUrl: './view-bookings.component.html',
  styleUrl: './view-bookings.component.scss',
})
export class ViewBookingsComponent {

  currentPage: any = 1;

  constructor(
    private customerService: CustomerService,
    private message: NzMessageService
  ) {
    this.getBookings();
  }

  getBookings() {
    this.customerService.getMyBookings(this.currentPage-1).subscribe(res => {
      console.log(res);
    }, error => {
      this.message
      .error(
        `${error.error}`,
        { nzDuration: 5000 }
      )
    })
  }
}
