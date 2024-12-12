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
  drafts: PostResponseDTO[] = [];
  published: PostResponseDTO[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.postService.getDraftPosts().subscribe(drafts => this.drafts = drafts);
    this.postService.getPublishedPosts().subscribe(published => this.published = published);
  }
}
