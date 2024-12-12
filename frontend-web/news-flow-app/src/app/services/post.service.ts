import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environment';

export interface PostDTO {
  title: string;
  content: string;
  author: string;
}

export interface PostResponseDTO {
  id: number;
  title: string;
  content: string;
  author: string;
  createdDate: string;
  isDraft: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}


  createPost(post: PostDTO): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/create`, post);
  }


  getDraftPosts(): Observable<PostResponseDTO[]> {
    return this.http.get<PostResponseDTO[]>(`${this.apiUrl}/drafts`);
  }


  getPublishedPosts(): Observable<PostResponseDTO[]> {
    return this.http.get<PostResponseDTO[]>(`${this.apiUrl}/published`);
  }


  editPost(postId: number, post: PostDTO): Observable<PostResponseDTO> {
    return this.http.put<PostResponseDTO>(`${this.apiUrl}/edit/${postId}`, post);
  }


  savePostAsDraft(postId: number): Observable<PostResponseDTO> {
    return this.http.post<PostResponseDTO>(`${this.apiUrl}/draft/${postId}`, {});
  }

  updatePost(postId: number): Observable<PostResponseDTO> {
    return this.http.put<PostResponseDTO>(`${this.apiUrl}/edit/${postId}`, {});
  }

}
