import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService, PostResponseDTO } from '../../services/post.service';
import { NavbarComponent } from "../navbar/navbar.component";
import { Comment, CommentService } from "../../services/comment.service";
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-post-list',
  standalone: true,
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css',
  imports: [CommonModule, NavbarComponent, FormsModule]
})
export class PostListComponent implements OnInit {
  published: PostResponseDTO[] = [];

  constructor(
    private postService: PostService,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getApprovedPosts().subscribe((posts) => {
      // Initialize each post with a temporary newCommentContent
      this.published = posts.map(post => ({
        ...post,
        newCommentContent: '' // Initialize newCommentContent for each post
      }));
    });
  }

  postComment(postId: number, newCommentContent: string): void {
    if (!newCommentContent.trim()) {
      return; // Don't submit if the comment is empty
    }

    // Call the service to post the comment
    this.commentService.createComment(postId, 'CurrentUsername', newCommentContent).subscribe(
      (newComment: Comment) => {
        // Find the post and add the new comment to its comment list
        const post = this.published.find(post => post.id === postId);
        if (post) {
          post.comments.push(newComment); // Push new comment into the post's comment array
          post.newCommentContent = ''; // Clear the comment input after posting
        }
      },
      error => {
        console.error('Error posting comment', error);
      }
    );
  }
}
