import { Routes } from '@angular/router';
import { PostListComponent } from './components/post-list/post-list.component';
import { CreatePostComponent } from './components/create-post/create-post.component';
import {DraftsListComponent} from "./components/drafts-list/drafts-list.component";
import {ReviewPostsComponent} from "./components/review-posts/review-posts.component";
import {AuthGuard} from "./services/authGuard";
import {LoginComponent} from "./components/login/login.component";

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'published', component: PostListComponent, canActivate: [AuthGuard]  },
  { path: 'drafts', component: DraftsListComponent, canActivate: [AuthGuard]  },
  { path: 'create', component: CreatePostComponent, canActivate: [AuthGuard]  },
  { path: 'edit/:id', component: CreatePostComponent, canActivate: [AuthGuard]  },
  { path: 'reviewPosts', component: ReviewPostsComponent, canActivate: [AuthGuard]  }
];


