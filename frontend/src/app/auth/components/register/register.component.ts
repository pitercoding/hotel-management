import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { DemoNgZorroAntdModule } from '../../../DemoNgZorroAntdModule';

@Component({
  selector: 'app-register.component',
  standalone: true,
  imports: [ReactiveFormsModule, DemoNgZorroAntdModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  registerForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.registerForm = this.fb.group({
      email: [null, [Validators.email, Validators.required]],
      password: [null, Validators.required],
      name: [null, Validators.required],
    });
  }
}
