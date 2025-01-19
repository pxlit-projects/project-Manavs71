import { TestBed, ComponentFixture, fakeAsync, tick, flush } from '@angular/core/testing';
import { DraftsListComponent } from './drafts-list.component';
import { PostService, PostResponseDTO, PostStatus } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('DraftsListComponent', () => {
  let component: DraftsListComponent;
  let fixture: ComponentFixture<DraftsListComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', [
      'getDraftPosts',
      'publishPost',
      'deletePost',
    ]);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getUsername', 'getUserRole']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, CommonModule],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jasmine.createSpy('get').and.returnValue(null), // Simulate no route params
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DraftsListComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    // Mocking AuthService to return a username
    authService.getUsername.and.returnValue('testUser');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display a message when there are no drafts', fakeAsync(() => {
    // Mocking getDraftPosts to return an empty list
    postService.getDraftPosts.and.returnValue(of<PostResponseDTO[]>([]));

    fixture.detectChanges();  // Trigger change detection
    tick();  // Simulate async tasks
    flush(); // Ensure all pending tasks are completed

    console.log('Drafts Loaded:', component.drafts); // Check the drafts data
    expect(component.drafts.length).toBe(0);  // Ensure no drafts are loaded
  }));

  it('should delete a draft', fakeAsync(() => {
    // Mocking getDraftPosts to return a list with one draft
    postService.getDraftPosts.and.returnValue(of<PostResponseDTO[]>([
      { id: 1, title: 'Draft Post 1', isDraft: true, author: 'testUser', createdDate: '2025-01-16T00:00:00' } as PostResponseDTO
    ]));

    fixture.detectChanges();  // Trigger change detection
    tick();  // Simulate async tasks
    flush(); // Ensure all pending tasks are completed

    // Check initial drafts loaded
    expect(component.drafts.length).toBe(1);

    // Spy on deletePost method
    postService.deletePost.and.returnValue(of({}));

    // Simulate clicking the delete button
    component.deletePost(1);

    fixture.detectChanges();  // Trigger change detection after deletion
    expect(postService.deletePost).toHaveBeenCalledWith(1);  // Ensure deletePost was called with correct id
    expect(component.drafts.length).toBe(0);  // Ensure the draft was deleted
  }));

  it('should publish a draft', fakeAsync(() => {
    // Mocking getDraftPosts to return a list with one draft
    postService.getDraftPosts.and.returnValue(of<PostResponseDTO[]>([
      { id: 1, title: 'Draft Post 1', isDraft: true, author: 'testUser', createdDate: '2025-01-16T00:00:00' } as PostResponseDTO
    ]));

    fixture.detectChanges();  // Trigger change detection
    tick();  // Simulate async tasks
    flush(); // Ensure all pending tasks are completed

    // Check initial drafts loaded
    expect(component.drafts.length).toBe(1);

    // Spy on publishPost method
    postService.publishPost.and.returnValue(of({} as PostResponseDTO));

    // Simulate clicking the publish button
    component.publishPost(1);

    fixture.detectChanges();  // Trigger change detection after publish
    expect(postService.publishPost).toHaveBeenCalledWith(1);  // Ensure publishPost was called with correct id
    expect(component.drafts.length).toBe(0);  // Ensure the draft is removed after publishing
  }));


  it('should not show admin actions if user is not an admin', fakeAsync(() => {
    // Mocking getDraftPosts to return a list with one draft
    postService.getDraftPosts.and.returnValue(of<PostResponseDTO[]>([
      { id: 1, title: 'Draft Post 1', isDraft: true, author: 'testUser', createdDate: '2025-01-16T00:00:00' } as PostResponseDTO
    ]));

    // Mocking getUserRole to return 'user'
    authService.getUserRole.and.returnValue('user');

    fixture.detectChanges();  // Trigger change detection
    tick();  // Simulate async tasks
    flush(); // Ensure all pending tasks are completed

    // Check if the admin-specific actions are not displayed
    const deleteButton = fixture.nativeElement.querySelector('.delete-button');
    const publishButton = fixture.nativeElement.querySelector('.publish-button');

    expect(deleteButton).toBeFalsy();  // Non-admin should not see delete button
    expect(publishButton).toBeFalsy();  // Non-admin should not see publish button
  }));







});
