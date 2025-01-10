import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../environment";

export interface Comment {
  id: number;
  postId: number;
  username: string;
  content: string;
  createdDate: string;
}

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private apiUrl = `${environment.commentServiceUrl}/comments`;

  constructor(private http: HttpClient) {}


  createComment(postId: number, username: string, content: string): Observable<Comment> {
    const url = `${this.apiUrl}/create`;
    const params = { postId: postId.toString(), username, content };
    return this.http.post<Comment>(url, null, { params });
  }


  getCommentsByPostId(postId: number): Observable<Comment[]> {
    const url = `${this.apiUrl}/${postId}`;
    return this.http.get<Comment[]>(url);
  }


  updateComment(id: number, content: string): Observable<Comment> {
    const url = `${this.apiUrl}/update/${id}`;
    const params = { content };
    return this.http.put<Comment>(url, null, { params });
  }


  deleteComment(id: number): Observable<void> {
    const url = `${this.apiUrl}/delete/${id}`;
    return this.http.delete<void>(url);
  }
}
