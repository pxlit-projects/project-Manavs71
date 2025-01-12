import { TestBed } from '@angular/core/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { ReviewService } from '../review.service';
import { PostService, PostResponseDTO, PostStatus } from '../post.service';
import {environment} from "../../../environment";
import {of} from "rxjs";


describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  let postService: PostService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [ReviewService, PostService]
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
    postService = TestBed.inject(PostService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch pending or rejected posts', () => {
    const publishedPosts: PostResponseDTO[] = [
      {
        id: 1,
        title: 'Pending Post',
        content: 'Content 1',
        author: 'Author 1',
        createdDate: '2025-01-12T18:05:16',
        isDraft: false,
        postStatus: PostStatus.PENDING,
        comments: [],
        newCommentContent: '',
        showComments: false,
        rejectionComment: ''
      },
      {
        id: 2,
        title: 'Approved Post',
        content: 'Content 2',
        author: 'Author 2',
        createdDate: '2025-01-12T18:05:16',
        isDraft: false,
        postStatus: PostStatus.APPROVED,
        comments: [],
        newCommentContent: '',
        showComments: false,
        rejectionComment: ''
      }
    ];



    service.getPendingOrRejectedPosts().subscribe(posts => {
      expect(posts.length).toBe(1);
      expect(posts[0].postStatus).toBe(PostStatus.PENDING);
    });
  });

  it('should approve a post', () => {
    const response: PostResponseDTO = {
      id: 1,
      title: 'Approved Post',
      content: 'Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: false,
      postStatus: PostStatus.APPROVED,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: ''
    };

    service.approvePost(1, 'Reviewer').subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.reviewServiceUrl}/review/approve/1?reviewer=Reviewer`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should reject a post', () => {
    const response: PostResponseDTO = {
      id: 1,
      title: 'Rejected Post',
      content: 'Content',
      author: 'Author',
      createdDate: '2025-01-12T18:05:16',
      isDraft: false,
      postStatus: PostStatus.REJECTED,
      comments: [],
      newCommentContent: '',
      showComments: false,
      rejectionComment: 'Needs more work'
    };

    service.rejectPost(1, 'Reviewer', 'Needs more work').subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.reviewServiceUrl}/review/reject/1?reviewer=Reviewer&comment=Needs more work`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });
});
