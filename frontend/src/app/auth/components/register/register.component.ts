import { AuthService, SignupRequest, SignupResponse } from './../../services/auth/auth.service';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { DemoNgZorroAntdModule } from '../../../DemoNgZorroAntdModule';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register.component',
  standalone: true,
  imports: [ReactiveFormsModule, DemoNgZorroAntdModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  registerForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private message: NzMessageService,
    private router: Router){}

  ngOnInit() {
    this.registerForm = this.fb.group({
      email: [null, [Validators.email, Validators.required]],
      password: [null, Validators.required],
      name: [null, Validators.required],
    });
  }

  submitForm() {
    console.log('submitForm called', this.registerForm.value, this.registerForm.valid);
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const payload = this.registerForm.getRawValue() as SignupRequest;
    this.authService.register(payload).subscribe({
      next: (res: SignupResponse) => {
        if (res.id != null) {
          this.message.success('Signup successful', { nzDuration: 5000 });
          this.router.navigateByUrl('/');
        } else {
          this.message.error(`${res.message ?? 'Signup failed'}`, { nzDuration: 5000 });
        }
      },
      error: (err) => {
        console.error('Signup error', err);
        const msg = err?.error?.message ?? err?.message ?? 'Signup failed';
        this.message.error(msg, { nzDuration: 5000 });
      },
    });
  }
}
