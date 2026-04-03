import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerRoutingModule } from './customer-routing.module';
import { CustomerComponent } from './customer.component';
import { RoomsComponent } from './components/rooms/rooms.component';
import { DemoNgZorroAntdModule } from '../../DemoNgZorroAntdModule';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    CustomerComponent,
    RoomsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    CustomerRoutingModule,
    DemoNgZorroAntdModule
  ],
})
export class CustomerModule {}
