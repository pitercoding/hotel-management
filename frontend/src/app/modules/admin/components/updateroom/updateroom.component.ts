import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import {
  AdminServices,
  RoomDto,
  UpdateRoomRequest,
} from '../../admin-services/admin.services';
import { DemoNgZorroAntdModule } from '../../../../DemoNgZorroAntdModule';

@Component({
  selector: 'app-updateroom.component',
  standalone: true,
  imports: [ReactiveFormsModule, DemoNgZorroAntdModule],
  templateUrl: './updateroom.component.html',
  styleUrl: './updateroom.component.scss',
})
export class UpdateRoomComponent implements OnInit {

  updateRoomForm: FormGroup;
  id: number;

  constructor(
    private fb: FormBuilder,
    private message: NzMessageService,
    private router: Router,
    private adminService: AdminServices,
    private activatedroute: ActivatedRoute
  ) {
    this.updateRoomForm = this.fb.group({
      name: ['', Validators.required],
      type: ['', Validators.required],
      price: ['', Validators.required],
    });
    this.id = Number(this.activatedroute.snapshot.params['id']);
  }

  ngOnInit(): void {
    if (!Number.isFinite(this.id) || this.id <= 0) {
      this.message.error('Invalid room id.', { nzDuration: 5000 });
      this.router.navigateByUrl('/admin/dashboard');
      return;
    }

    this.getRoomById();
  }

  submitForm() {
    if (this.updateRoomForm.invalid) {
      this.updateRoomForm.markAllAsTouched();
      this.message.warning('Fill in the room details before updating.', {
        nzDuration: 5000,
      });
      return;
    }

    const roomDto: UpdateRoomRequest = {
      name: this.updateRoomForm.get('name')?.value?.trim() ?? '',
      type: this.updateRoomForm.get('type')?.value?.trim() ?? '',
      price: Number(this.updateRoomForm.get('price')?.value),
    };

    this.adminService.updateRoom(this.id, roomDto).subscribe({
      next: () => {
        this.message.success('Room updated successfully.', {
          nzDuration: 5000,
        });
        this.router.navigateByUrl('/admin/dashboard');
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to update the room right now.';

        this.message.error(errorMessage, { nzDuration: 5000 });
      },
    });
  }

  getRoomById() {
    this.adminService.getRoomById(this.id).subscribe({
      next: (res: RoomDto) => {
        this.updateRoomForm.patchValue({
          name: res.name,
          type: res.type,
          price: res.price,
        });
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to load the room right now.';

        this.message.error(errorMessage, { nzDuration: 5000 });
        this.router.navigateByUrl('/admin/dashboard');
      },
    });
  }
}
