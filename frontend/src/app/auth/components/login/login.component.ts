import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DemoNgZorroAntdModule } from '../../../DemoNgZorroAntdModule';
import { AuthService, LoginRequest, LoginResponse } from '../../services/auth/auth.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';
import { UserStorageService } from '../../services/storage/user-storage.service';

@Component({
  selector: 'app-login.component',
  standalone: true,
  imports: [ReactiveFormsModule, DemoNgZorroAntdModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {

  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private message: NzMessageService,
    private router: Router){}

  ngOnInit() {
    this.loginForm = this.fb.group({
      email: [null, [Validators.email, Validators.required]],
      password: [null, Validators.required],
    });
  }

  submitForm() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const payload = this.loginForm.getRawValue() as LoginRequest;
    this.authService.login(payload).subscribe({
      next: (res: LoginResponse) => {
        UserStorageService.signOut();
        UserStorageService.saveToken(res.jwt);
        UserStorageService.saveUser({ id: res.userId, role: res.userRole });
        this.message.success('Login successful', { nzDuration: 3000 });

        if(UserStorageService.isAdminLoggedIn()) {
          this.router.navigateByUrl('/admin/dashboard');
        } else if(UserStorageService.isCustomerLoggedIn()) {
          this.router.navigateByUrl('/customer/rooms');
        }
      },
      error: () => {
        this.message.error('Bad credentials', { nzDuration: 5000 });
      },
    });
  }

}
