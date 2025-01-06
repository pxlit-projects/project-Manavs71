import { Component } from '@angular/core';
import {CommonModule, DatePipe, NgForOf} from "@angular/common";
import {PostResponseDTO, PostService} from "../../services/post.service";

import { Router, ActivatedRoute } from "@angular/router";
import {NavbarComponent} from "../navbar/navbar.component";  // Correct way to import Router and ActivatedRoute

@Component({
  selector: 'app-drafts-list',
  standalone: true,
    imports: [
        DatePipe,
        NgForOf,
        CommonModule,
        NavbarComponent,
    ],
  templateUrl: './drafts-list.component.html',
  styleUrl: './drafts-list.component.css',

})
export class DraftsListComponent {
  drafts: PostResponseDTO[] = [];
  published: PostResponseDTO[] = [];

  constructor(
    private postService: PostService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getDraftPosts().subscribe(drafts => this.drafts = drafts);
    this.postService.getPublishedPosts().subscribe(published => this.published = published);
  }

  editPost(postId: number): void {
    console.log(`Editing post with ID: ${postId}`);
    // Add navigation or edit functionality here
    this.router.navigate(['/edit', postId]); // Navigate to the edit route

  }

  deletePost(postId: number): void {
    this.postService.deletePost(postId).subscribe({
      next: () => {
        // Remove the deleted post from the list
        this.drafts = this.drafts.filter(draft => draft.id !== postId);
      },
      error: (err) => {
        console.error('Error deleting post:', err);
      },
    });
  }

  publishPost(postId: number): void {
    this.postService.publishPost(postId).subscribe({
      next: () => {
        // Remove the deleted post from the list
        this.drafts = this.drafts.filter(draft => draft.id !== postId);
      },
      error: (err) => {
        console.error('Error deleting post:', err);
      },
    });
  }

}
