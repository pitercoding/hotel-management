import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { DemoNgZorroAntdModule } from '../../../../DemoNgZorroAntdModule';
import { PostRoomComponent } from './post-room.component';

describe('PostRoomComponent', () => {
  let component: PostRoomComponent;
  let fixture: ComponentFixture<PostRoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PostRoomComponent],
      imports: [ReactiveFormsModule, DemoNgZorroAntdModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
