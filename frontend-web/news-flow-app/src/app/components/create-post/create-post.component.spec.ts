import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { CreatePostComponent } from './create-post.component';
import { PostService, PostResponseDTO, PostStatus } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';

describe('CreatePostComponent', () => {
  let component: CreatePostComponent;
  let fixture: ComponentFixture<CreatePostComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', [
      'getDraftPosts',
      'createPost',
      'editPost',
      'createDraft',
    ]);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getUsername', 'getUserRole']);

    await TestBed.configureTestingModule({
      imports: [FormsModule],
      providers: [
        // Add routes to the provideRouter function
        provideRouter([
          { path: 'published', component: CreatePostComponent }, // Mock route for '/published'
          { path: 'drafts', component: CreatePostComponent }, // Mock route for '/drafts'
        ]),
        { provide: PostService, useValue: postServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jasmine.createSpy('get').and.returnValue(null), // Simulating no postId in route
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CreatePostComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    // Mocking getDraftPosts method to return an observable with mock data
    postService.getDraftPosts.and.returnValue(of([{
      id: 1,
      title: 'Test Post',
      content: 'Content',
      author: 'Author',
      createdDate: '',
      isDraft: true,
      postStatus: PostStatus.PENDING,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    }]));

    // Mocking createPost method
    postService.createPost.and.returnValue(of({} as PostResponseDTO));

    // Mocking createDraft method
    postService.createDraft.and.returnValue(of({} as PostResponseDTO));

    // Mocking editPost method
    postService.editPost.and.returnValue(of({} as PostResponseDTO));

    // Mocking authService to return a username
    authService.getUsername.and.returnValue('testUser');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call createPost on form submission in create mode', fakeAsync(() => {
    // Simulate the form submission in create mode
    component.post.title = 'New Post';
    component.post.content = 'This is a new post';
    component.submitPost({ valid: true });

    // Ensure createPost was called
    expect(postService.createPost).toHaveBeenCalled();
  }));

  it('should call editPost on form submission in edit mode', fakeAsync(() => {
    // Simulate entering edit mode
    component.isEditMode = true;
    component.post.title = 'Updated Post';
    component.post.content = 'Updated content';
    component.submitPost({ valid: true });

    // Ensure editPost was called
    expect(postService.editPost).toHaveBeenCalled();
  }));



  it('should mark form as invalid if title is empty', fakeAsync(() => {
    // Simulate an empty title field
    component.post.title = '';
    component.post.content = 'This is a post with no title';
    fixture.detectChanges(); // Trigger change detection

    const form = fixture.debugElement.nativeElement.querySelector('form');
    expect(form.checkValidity()).toBeFalse(); // Form should be invalid
  }));

  it('should mark form as invalid if content is empty', fakeAsync(() => {
    // Simulate an empty content field
    component.post.title = 'Valid Title';
    component.post.content = '';
    fixture.detectChanges(); // Trigger change detection

    const form = fixture.debugElement.nativeElement.querySelector('form');
    expect(form.checkValidity()).toBeFalse(); // Form should be invalid
  }));


});
