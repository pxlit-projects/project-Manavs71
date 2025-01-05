import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DraftsListComponent } from './drafts-list.component';

describe('DraftsListComponent', () => {
  let component: DraftsListComponent;
  let fixture: ComponentFixture<DraftsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DraftsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DraftsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
