  import { Component, OnInit } from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { PostService, PostResponseDTO } from '../../services/post.service';
  @Component({
    selector: 'app-post-list',
    standalone: true,
    templateUrl: './post-list.component.html',
    styleUrl: './post-list.component.css',
    imports: [CommonModule]
  })
  export class PostListComponent {
    published: PostResponseDTO[] = [];

    constructor(private postService: PostService) {}

    ngOnInit(): void {
      this.loadPosts();
    }

    loadPosts(): void {
      this.postService.getApprovedPosts().subscribe(published => this.published = published);
    }

    editPost(postId: number): void {
      console.log(`Editing post with ID: ${postId}`);
      // Add navigation or edit functionality here
    }


  }
