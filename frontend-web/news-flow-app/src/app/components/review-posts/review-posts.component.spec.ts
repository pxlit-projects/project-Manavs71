import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewPostsComponent } from './review-posts.component';

describe('ReviewPostsComponent', () => {
  let component: ReviewPostsComponent;
  let fixture: ComponentFixture<ReviewPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewPostsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
