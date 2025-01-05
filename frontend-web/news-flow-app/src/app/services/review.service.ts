import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environment';
import {PostResponseDTO, PostService} from "./post.service";

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.reviewServiceUrl}/api/review`;

  constructor(
    private http: HttpClient,
    private postService: PostService // Inject PostService to reuse its methods
  ) {}

  getPendingOrRejectedPosts(): Observable<PostResponseDTO[]> {
    return new Observable((observer) => {
      // Fetch both drafts and published posts
      this.postService.getPublishedPosts().subscribe((published) => {
          // Combine drafts and published posts
          const allPosts = [...published];
          // Filter the posts with PENDING or REJECTED status
          const filteredPosts = allPosts.filter(
            post => post.postStatus === 'PENDING' || post.postStatus === 'REJECTED'
          );
          observer.next(filteredPosts);
          observer.complete();
        });
      });
  }

  approvePost(postId: number, reviewer: string): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/approve/${postId}?reviewer=${reviewer}`, {});
  }

  rejectPost(postId: number, reviewer: string, comment: string): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/reject/${postId}?reviewer=${reviewer}&comment=${comment}`, {});
  }
}
