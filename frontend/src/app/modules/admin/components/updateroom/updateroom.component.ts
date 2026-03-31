import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import { AdminServices } from '../../admin-services/admin.services';
import { DemoNgZorroAntdModule } from '../../../../DemoNgZorroAntdModule';

@Component({
  selector: 'app-updateroom.component',
  standalone: true,
  imports: [ReactiveFormsModule, DemoNgZorroAntdModule],
  templateUrl: './updateroom.component.html',
  styleUrl: './updateroom.component.scss',
})
export class UpdateRoomComponent {

  updateRoomForm: FormGroup;
  id: string;

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
    this.id = this.activatedroute.snapshot.params['id'];
  }

  submitForm() {

  }
}
