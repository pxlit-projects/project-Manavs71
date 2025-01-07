import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService, PostResponseDTO } from '../../services/post.service';
import { NavbarComponent } from "../navbar/navbar.component";
import { Comment, CommentService } from "../../services/comment.service";
import { FormsModule } from "@angular/forms";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-post-list',
  standalone: true,
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css',
  imports: [CommonModule, NavbarComponent, FormsModule]
})
export class PostListComponent implements OnInit {
  published: PostResponseDTO[] = [];
  username: string | null;

  currentEditingCommentId: number | null = null;
  editingContent: string = '';
  constructor(
    private postService: PostService,
    private commentService: CommentService, private authService: AuthService
  ) {
    this.username = authService.getUsername();

  }

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

    if (!this.username) {
      console.error('User is not logged in or username is null');
      return;
    }

    // Call the service to post the comment
    this.commentService.createComment(postId, this.username, newCommentContent).subscribe(
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

  // Enable editing mode for a comment
  // Enable editing for a specific comment
  enableEditing(commentId: number, content: string): void {
    this.currentEditingCommentId = commentId;
    this.editingContent = content;
  }

  // Cancel editing mode
  cancelEditing(): void {
    this.currentEditingCommentId = null;
    this.editingContent = '';
  }

  // Update the comment
  updateComment(commentId: number): void {
    if (!this.editingContent.trim()) {
      return; // Prevent empty updates
    }

    this.commentService.updateComment(commentId, this.editingContent).subscribe(
      (updatedComment: Comment) => {
        // Find the post and comment to update
        for (const post of this.published) {
          const comment = post.comments.find(c => c.id === commentId);
          if (comment) {
            comment.content = updatedComment.content; // Update the displayed content
            break;
          }
        }

        // Exit editing mode
        this.cancelEditing();
      },
      error => {
        console.error('Error updating comment', error);
      }
    );
  }
}
