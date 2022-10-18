import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadChunkFileComponent } from './upload-chunk-file.component';

describe('UploadChunkFileComponent', () => {
  let component: UploadChunkFileComponent;
  let fixture: ComponentFixture<UploadChunkFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadChunkFileComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadChunkFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
