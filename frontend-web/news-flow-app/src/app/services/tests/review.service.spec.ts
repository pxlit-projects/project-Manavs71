import { TestBed } from '@angular/core/testing';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import { of } from 'rxjs';
import {provideHttpClient} from "@angular/common/http";
import {ReviewService} from "../review.service";
import {PostResponseDTO, PostService, PostStatus} from "../post.service";
import {environment} from "../../../environment";

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  let postServiceSpy: jasmine.SpyObj<PostService>;

  beforeEach(() => {
    const postServiceMock = jasmine.createSpyObj('PostService', ['getPublishedPosts']);

    TestBed.configureTestingModule({
      providers: [
        ReviewService,
        { provide: PostService, useValue: postServiceMock },
        provideHttpClient(),
        provideHttpClientTesting()
      ],
    });

    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
    postServiceSpy = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no outstanding HTTP requests
  });

  const mockPosts: PostResponseDTO[] = [
    { id: 1, title: 'Post 1', content: 'Content 1', author: 'Author 1', createdDate: '2023-10-01', isDraft: false, postStatus: PostStatus.PENDING, comments: [], newCommentContent: '', showComments: false, rejectionComment: '' },
    { id: 2, title: 'Post 2', content: 'Content 2', author: 'Author 2', createdDate: '2023-10-02', isDraft: false, postStatus: PostStatus.APPROVED, comments: [], newCommentContent: '', showComments: false, rejectionComment: '' },
    { id: 3, title: 'Post 3', content: 'Content 3', author: 'Author 3', createdDate: '2023-10-03', isDraft: false, postStatus: PostStatus.REJECTED, comments: [], newCommentContent: '', showComments: false, rejectionComment: 'Needs revision' },
  ];

  // Test for getPendingOrRejectedPosts
  it('should fetch pending or rejected posts', () => {
    postServiceSpy.getPublishedPosts.and.returnValue(of(mockPosts));

    service.getPendingOrRejectedPosts().subscribe((filteredPosts) => {
      expect(filteredPosts.length).toBe(2); // Only PENDING and REJECTED
      expect(filteredPosts).toEqual([
        mockPosts[0], // PENDING
        mockPosts[2], // REJECTED
      ]);
    });

    expect(postServiceSpy.getPublishedPosts).toHaveBeenCalled();
  });
  // Test for approvePost
  it('should approve a post', () => {
    const postId = 1;
    const reviewer = 'ReviewerName';
    const approvedPost: PostResponseDTO = { ...mockPosts[0], postStatus: PostStatus.APPROVED };

    service.approvePost(postId, reviewer).subscribe((res) => {
      expect(res).toEqual(approvedPost);
    });

    const req = httpMock.expectOne(`${environment.reviewServiceUrl}/review/approve/${postId}?reviewer=${reviewer}`);
    expect(req.request.method).toBe('POST');
    req.flush(approvedPost);
  });

  // Test for rejectPost
  it('should reject a post', () => {
    const postId = 1;
    const reviewer = 'ReviewerName';
    const comment = 'This post requires changes.';
    const rejectedPost: PostResponseDTO = { ...mockPosts[0], postStatus: PostStatus.REJECTED, rejectionComment: comment };

    service.rejectPost(postId, reviewer, comment).subscribe((res) => {
      expect(res).toEqual(rejectedPost);
    });

    const req = httpMock.expectOne(`${environment.reviewServiceUrl}/review/reject/${postId}?reviewer=${reviewer}&comment=${comment}`);
    expect(req.request.method).toBe('POST');
    req.flush(rejectedPost);
  });

});
