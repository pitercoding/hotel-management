import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import {
  AdminServices,
  PostRoomRequest,
} from '../../admin-services/admin.services';

@Component({
  selector: 'app-post-room',
  standalone: false,
  templateUrl: './post-room.component.html',
  styleUrl: './post-room.component.scss',
})
export class PostRoomComponent {
  roomDetailsForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private message: NzMessageService,
    private router: Router,
    private adminService: AdminServices
  ) {
    this.roomDetailsForm = this.fb.group({
      name: ['', Validators.required],
      type: ['', Validators.required],
      price: ['', Validators.required],
    });
  }

  submitForm() {
    if (this.roomDetailsForm.invalid) {
      this.roomDetailsForm.markAllAsTouched();
      this.message.warning('Fill in the room details before saving.', {
        nzDuration: 5000,
      });
      return;
    }

    const roomDto: PostRoomRequest = {
      name: this.roomDetailsForm.get('name')?.value?.trim() ?? '',
      type: this.roomDetailsForm.get('type')?.value?.trim() ?? '',
      price: Number(this.roomDetailsForm.get('price')?.value),
    };

    this.adminService.postRoomDetails(roomDto).subscribe({
      next: () => {
        this.message.success('Room posted successfully.', {
          nzDuration: 5000,
        });
        this.router.navigateByUrl('/admin/rooms');
      },
      error: (error) => {
        const errorMessage =
          typeof error?.error === 'string' && error.error.trim().length > 0
            ? error.error
            : 'Unable to post the room right now.';

        this.message.error(errorMessage, {
          nzDuration: 5000,
        });
      },
    });
  }
}
