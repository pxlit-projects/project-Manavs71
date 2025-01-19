import { TestBed } from '@angular/core/testing';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import { PostService, PostDTO, PostResponseDTO, PostStatus } from '../post.service';
import { environment } from '../../../environment';
import {provideHttpClient} from "@angular/common/http";

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PostService,provideHttpClient(),provideHttpClientTesting()], // Provide the service
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no unmatched requests
  });

  const mockPost: PostResponseDTO = {
    id: 1,
    title: 'Mock Post',
    content: 'Mock Content',
    author: 'Mock Author',
    createdDate: '2023-10-01T00:00:00Z',
    isDraft: false,
    postStatus: PostStatus.APPROVED,
    comments: [],
    newCommentContent: '',
    showComments: false,
    rejectionComment: '',
  };

  const mockPosts: PostResponseDTO[] = [mockPost];

  // Test for createPost
  it('should create a new post', () => {
    const newPost: PostDTO = { title: 'New Post', content: 'Content', author: 'Author' };

    service.createPost(newPost).subscribe((res) => {
      expect(res).toEqual(mockPost);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/create`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newPost);
    req.flush(mockPost);
  });

  it('should create a draft post', () => {
    const draftPost: PostDTO = { title: 'Draft Post', content: 'Draft Content', author: 'Draft Author' };

    service.createDraft(draftPost).subscribe((res) => {
      expect(res).toEqual(mockPost);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/createDraft`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(draftPost);
    req.flush(mockPost);
  });

  // Test for getDraftPosts
  it('should fetch draft posts', () => {
    service.getDraftPosts().subscribe((res) => {
      expect(res).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/drafts`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  // Test for getPublishedPosts
  it('should fetch published posts', () => {
    service.getPublishedPosts().subscribe((res) => {
      expect(res).toEqual(mockPosts);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/published`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  // Test for getApprovedPosts
  it('should fetch approved posts', () => {
    const approvedPost: PostResponseDTO = { ...mockPost, postStatus: PostStatus.APPROVED };
    const mockPostsWithDifferentStatuses: PostResponseDTO[] = [
      approvedPost,
      { ...mockPost, postStatus: PostStatus.REJECTED },
    ];

    service.getApprovedPosts().subscribe((res) => {
      expect(res).toEqual([approvedPost]); // Should only return approved posts
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/published`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPostsWithDifferentStatuses);
  });

  // Test for editPost
  it('should edit a post', () => {
    const updatedPost: PostDTO = { title: 'Updated Post', content: 'Updated Content', author: 'Updated Author' };

    service.editPost(1, updatedPost).subscribe((res) => {
      expect(res).toEqual(mockPost);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/edit/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedPost);
    req.flush(mockPost);
  });


  // Test for deletePost
  it('should delete a post', () => {
    service.deletePost(1).subscribe((res) => {
      expect(res).toBeNull();
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/draft/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null); // No response body for delete
  });

  // Test for savePostAsDraft
  it('should save a post as draft', () => {
    service.savePostAsDraft(1).subscribe((res) => {
      expect(res).toEqual(mockPost);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/draft/1`);
    expect(req.request.method).toBe('POST');
    req.flush(mockPost);
  });

  // Test for publishPost
  it('should publish a post', () => {
    service.publishPost(1).subscribe((res) => {
      expect(res).toEqual(mockPost);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/publish/1`);
    expect(req.request.method).toBe('POST');
    req.flush(mockPost);
  });

  // Test for updatePost
  it('should update a post', () => {
    service.updatePost(1).subscribe((res) => {
      expect(res).toEqual(mockPost);
    });

    const req = httpMock.expectOne(`${environment.postServiceUrl}/posts/edit/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockPost);
  });

});
