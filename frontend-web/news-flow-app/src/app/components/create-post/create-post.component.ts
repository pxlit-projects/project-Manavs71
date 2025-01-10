import {Component, OnInit} from '@angular/core';
import {PostResponseDTO, PostService, PostStatus} from "../../services/post.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {CommonModule} from "@angular/common";
import {NavbarComponent} from "../navbar/navbar.component";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    MatFormFieldModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    CommonModule,
    NavbarComponent
  ],
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.css']
})
export class CreatePostComponent implements OnInit {
  post: PostResponseDTO = { title: '', content: '', author: '', id: 0, createdDate: '', isDraft: false , postStatus: PostStatus.PENDING, comments: [], newCommentContent: '', showComments: false, rejectionComment: ''};
  isEditMode: boolean = false;
  postId!: number;

  constructor(
    private postService: PostService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const postId = this.route.snapshot.paramMap.get('id');
    if (postId != null) {
      this.postId = +postId;  // Convert to number
      this.isEditMode = true;
      this.loadPost(this.postId);
    }
  }

  loadPost(postId: number): void {
    this.postService.getDraftPosts().subscribe(posts => {
      const post = posts.find(p => p.id === postId);
      if (post) {
        this.post = { ...post }; // Fill form with the post data
      }
    });
  }

  submitPost(postForm: any): void {
    if (postForm.valid) {
      if (this.isEditMode) {
        this.post.author = this.authService.getUsername();
        this.postService.editPost(this.postId, this.post).subscribe(response => {
          this.router.navigate(['/drafts']);
        });
      } else {

        //set author
        this.post.author = this.authService.getUsername();
        console.log(this.post.author);

        this.postService.createPost(this.post).subscribe(response => {
          this.router.navigate(['/published']);
        });
      }
    } else {
      // Optional: Handle form submission when invalid
      console.log("Form is invalid.");
    }
  }

  saveAsDraft(): void {
    const draftPost = { ...this.post, isDraft: true }; // Flag as draft
    draftPost.author = this.authService.getUsername();
    this.postService.createDraft(draftPost).subscribe(response => {
      this.router.navigate(['/drafts']); // Redirect to drafts list after saving
    });
  }
}
