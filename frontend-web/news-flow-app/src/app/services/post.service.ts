import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {map, Observable} from 'rxjs';
import { environment } from '../../environment';
import {Comment} from "./comment.service";

export interface PostDTO {
  title: string;
  content: string;
  author: string | null;
}

export enum PostStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
}

export interface PostResponseDTO {
  id: number;
  title: string;
  content: string;
  author: string | null;
  createdDate: string;
  isDraft: boolean;
  postStatus: PostStatus
  comments: Comment[] ;
  newCommentContent: string;
  showComments: boolean;
  rejectionComment: string;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = `${environment.postServiceUrl}/posts`;

  constructor(private http: HttpClient) {}


  createPost(post: PostDTO): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/create`, post);
  }

  createDraft(post: PostDTO): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/createDraft`, post);
  }


  getDraftPosts(): Observable<PostResponseDTO[]> {
    return this.http.get<PostResponseDTO[]>(`${this.apiUrl}/drafts`);
  }


  getPublishedPosts(): Observable<PostResponseDTO[]> {
    return this.http.get<PostResponseDTO[]>(`${this.apiUrl}/published`);
  }

  getApprovedPosts(): Observable<PostResponseDTO[]> {
    return this.http.get<PostResponseDTO[]>(`${this.apiUrl}/published`).pipe(
      map(posts => posts.filter(post => post.postStatus === PostStatus.APPROVED))  // Filter out only approved posts
    );
  }


  editPost(postId: number, post: PostDTO): Observable<PostResponseDTO> {
    return this.http.put<PostResponseDTO>(`${this.apiUrl}/edit/${postId}`, post);
  }

  deletePost(postId: number) {
    return this.http.delete(`${this.apiUrl}/draft/${postId}`);
  }



  savePostAsDraft(postId: number): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/draft/${postId}`, {});
  }

  publishPost(postId: number): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/publish/${postId}`, {});
  }

  updatePost(postId: number): Observable<PostResponseDTO> {
    return this.http.put<PostResponseDTO>(`${this.apiUrl}/edit/${postId}`, {});
  }

}
