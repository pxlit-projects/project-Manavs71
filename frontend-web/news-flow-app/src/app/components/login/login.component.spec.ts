import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['login', 'getUserRole']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [FormsModule, CommonModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should submit valid credentials and redirect', fakeAsync(() => {
    // Mock the login method to return true and mock the user role
    authService.login.and.returnValue(true);
    authService.getUserRole.and.returnValue('Redacteur');  // Mocking role

    component.username = 'validUser';
    component.password = 'validPassword';

    component.onSubmit();  // Call onSubmit method

    tick();  // Simulate async tasks

    expect(router.navigate).toHaveBeenCalledWith(['/published']);  // Check if the router navigated
  }));

  it('should display error message for invalid credentials', fakeAsync(() => {
    // Mock the login method to return false for invalid credentials
    authService.login.and.returnValue(false);

    component.username = 'invalidUser';
    component.password = 'invalidPassword';

    component.onSubmit();  // Call onSubmit method

    tick();  // Simulate async tasks

    expect(component.errorMessage).toBe('Invalid username or password');  // Check if error message is displayed
  }));

  it('should redirect to published page if role is Redacteur', fakeAsync(() => {
    // Mock the login method to return true and the user role as 'Redacteur'
    authService.login.and.returnValue(true);
    authService.getUserRole.and.returnValue('Redacteur');

    component.username = 'testUser';
    component.password = 'password';

    component.onSubmit();  // Call onSubmit method

    tick();  // Simulate async tasks

    expect(router.navigate).toHaveBeenCalledWith(['/published']);  // Check if the router navigated to /published
  }));

  it('should redirect to published page for other roles', fakeAsync(() => {
    // Mock the login method to return true and a different role
    authService.login.and.returnValue(true);
    authService.getUserRole.and.returnValue('Admin');  // Different role for testing

    component.username = 'adminUser';
    component.password = 'adminPassword';

    component.onSubmit();  // Call onSubmit method

    tick();  // Simulate async tasks

    expect(router.navigate).toHaveBeenCalledWith(['/published']);  // Check if the router navigated to /published
  }));
});
