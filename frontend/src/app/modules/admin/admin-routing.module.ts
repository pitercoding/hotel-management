import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { PostRoomComponent } from './components/post-room/post-room.component';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'rooms' },
  { path: 'dashboard', redirectTo: 'rooms', pathMatch: 'full' },
  { path: 'rooms', component: DashboardComponent },
  { path: 'room', component: PostRoomComponent },
  { path: 'reservations', redirectTo: 'rooms', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
