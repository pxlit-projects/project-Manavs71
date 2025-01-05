import { Routes } from '@angular/router';
import { PostListComponent } from './components/post-list/post-list.component';
import { CreatePostComponent } from './components/create-post/create-post.component';
import {DraftsListComponent} from "./components/drafts-list/drafts-list.component";
import {ReviewPostsComponent} from "./components/review-posts/review-posts.component";

export const routes: Routes = [
  { path: '', redirectTo: '/published', pathMatch: 'full' },
  { path: 'published', component: PostListComponent },
  { path: 'drafts', component: DraftsListComponent },
  { path: 'create', component: CreatePostComponent },
  { path: 'edit/:id', component: CreatePostComponent },
  { path: 'reviewPosts', component: ReviewPostsComponent }
];


