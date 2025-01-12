import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostService, PostDTO, PostResponseDTO, PostStatus } from '../post.service';
import { environment } from '../../../environment';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostService]
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create a new post', () => {
    const post: PostDTO = { title: 'Test Post', content: 'Test Content', author: 'Author' };
    const response: PostResponseDTO = {
      id: 1,
      title: 'Test Post',
      content: 'Test Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: false,
      postStatus: PostStatus.PENDING,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.createPost(post).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/create`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should create a new draft post', () => {
    const post: PostDTO = { title: 'Draft Post', content: 'Draft Content', author: 'Author' };
    const response: PostResponseDTO = {
      id: 2,
      title: 'Draft Post',
      content: 'Draft Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: true,
      postStatus: PostStatus.PENDING,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.createDraft(post).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/createDraft`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should fetch draft posts', () => {
    const response: PostResponseDTO[] = [
      {
        id: 1,
        title: 'Draft Post 1',
        content: 'Content 1',
        author: 'Author 1',
        createdDate: '2025-01-12T18:05:16',
        isDraft: true,
        postStatus: PostStatus.PENDING,
        comments: [],
        newCommentContent: '',
        showComments: false,
        rejectionComment: ''
      }
    ];

    service.getDraftPosts().subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/drafts`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should fetch published posts', () => {
    const response: PostResponseDTO[] = [
      {
        id: 1,
        title: 'Published Post 1',
        content: 'Content 1',
        author: 'Author 1',
        createdDate: '2025-01-12T18:05:16',
        isDraft: false,
        postStatus: PostStatus.APPROVED,
        comments: [],
        newCommentContent: '',
        showComments: false,
        rejectionComment: ''
      }
    ];

    service.getPublishedPosts().subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/published`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should fetch approved posts', () => {
    const response: PostResponseDTO[] = [
      {
        id: 1,
        title: 'Approved Post 1',
        content: 'Content 1',
        author: 'Author 1',
        createdDate: '2025-01-12T18:05:16',
        isDraft: false,
        postStatus: PostStatus.APPROVED,
        comments: [],
        newCommentContent: '',
        showComments: false,
        rejectionComment: ''
      },
      {
        id: 2,
        title: 'Pending Post 1',
        content: 'Content 2',
        author: 'Author 2',
        createdDate: '2025-01-12T18:05:16',
        isDraft: false,
        postStatus: PostStatus.PENDING,
        comments: [],
        newCommentContent: '',
        showComments: false,
        rejectionComment: ''
      }
    ];

    service.getApprovedPosts().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].postStatus).toBe(PostStatus.APPROVED);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/published`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should edit a post', () => {
    const post: PostDTO = { title: 'Edited Post', content: 'Edited Content', author: 'Author' };
    const response: PostResponseDTO = {
      id: 1,
      title: 'Edited Post',
      content: 'Edited Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: false,
      postStatus: PostStatus.PENDING,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.editPost(1, post).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/edit/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(response);
  });

  it('should delete a post', () => {
    service.deletePost(1).subscribe(res => {
      expect(res).toBeNull();
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/draft/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should save a post as draft', () => {
    const response: PostResponseDTO = {
      id: 1,
      title: 'Draft Post',
      content: 'Draft Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: true,
      postStatus: PostStatus.PENDING,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.savePostAsDraft(1).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/draft/1`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should publish a post', () => {
    const response: PostResponseDTO = {
      id: 1,
      title: 'Published Post',
      content: 'Published Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: false,
      postStatus: PostStatus.APPROVED,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.publishPost(1).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/publish/1`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should update a post', () => {
    const response: PostResponseDTO = {
      id: 1,
      title: 'Updated Post',
      content: 'Updated Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: false,
      postStatus: PostStatus.PENDING,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.updatePost(1).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/edit/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(response);
  });
});
