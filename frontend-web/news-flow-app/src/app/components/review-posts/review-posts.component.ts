import { Component } from '@angular/core';
import {PostResponseDTO} from "../../services/post.service";
import {ReviewService} from "../../services/review.service";
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-review-posts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review-posts.component.html',
  styleUrl: './review-posts.component.css'
})
export class ReviewPostsComponent {
  posts: PostResponseDTO[] = [];

  constructor(
    private reviewService: ReviewService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  // Load posts with PENDING or REJECTED status
  loadPosts(): void {
    this.reviewService.getPendingOrRejectedPosts().subscribe((posts) => {
      this.posts = posts;
    });
  }

  // Approve post
  approvePost(postId: number): void {
    const reviewer = 'manav';
    this.reviewService.approvePost(postId, reviewer).subscribe((post) => {
      this.posts = this.posts.filter(p => p.id !== postId); // Remove approved post from the list
    });
  }

  // Reject post
  rejectPost(postId: number): void {
    const reviewer = 'manav';
    const comment = 'Reason for rejection'; // Replace with a comment input
    this.reviewService.rejectPost(postId, reviewer, comment).subscribe((post) => {
      this.posts = this.posts.filter(p => p.id !== postId); // Remove rejected post from the list
    });
  }
}
