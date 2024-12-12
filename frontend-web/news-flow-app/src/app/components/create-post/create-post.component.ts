import { Component } from '@angular/core';
import {PostDTO, PostService} from "../../services/post.service";
import {Router} from "@angular/router";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    MatFormFieldModule,
    FormsModule
  ],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.css'
})
export class CreatePostComponent {
  post: PostDTO = { title: '', content: '', author: '' };

  constructor(private postService: PostService, private router: Router) {}

  createPost(): void {
    this.postService.createPost(this.post).subscribe(response => {
      this.router.navigate(['/posts']); // Redirect to posts list after creation
    });
  }
}
