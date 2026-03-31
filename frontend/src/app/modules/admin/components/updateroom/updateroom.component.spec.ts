import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateRoomComponent } from './updateroom.component';

describe('UpdateroomComponent', () => {
  let component: UpdateRoomComponent;
  let fixture: ComponentFixture<UpdateRoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateRoomComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateRoomComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
