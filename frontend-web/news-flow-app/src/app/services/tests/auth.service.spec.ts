import { TestBed } from '@angular/core/testing';
import {AuthService} from "../auth.service";

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthService], // Provide the AuthService
    });
    service = TestBed.inject(AuthService);
  });

  // Test for login with valid credentials
  it('should log in successfully with valid credentials', () => {
    const username = 'manav';
    const password = 'pxl';

    const loginResult = service.login(username, password);

    expect(loginResult).toBeTrue(); // Login should succeed
    expect(service.getUsername()).toBe(username); // Username should be set
    expect(service.getUserRole()).toBe('Editor'); // Role should be set
  });

  // Test for login with invalid credentials
  it('should fail login with invalid credentials', () => {
    const username = 'manav';
    const password = 'wrongPassword';

    const loginResult = service.login(username, password);

    expect(loginResult).toBeFalse(); // Login should fail
  });

  // Test for retrieving the role of a logged-in user
  it('should retrieve the user role after login', () => {
    const username = 'sofia';
    const password = 'pxl';

    service.login(username, password);
    const role = service.getUserRole();

    expect(role).toBe('Editor'); // The role should match the logged-in user
  });

  // Test for retrieving the username of a logged-in user
  it('should retrieve the username after login', () => {
    const username = 'john';
    const password = 'pxl';

    service.login(username, password);
    const loggedInUsername = service.getUsername();

    expect(loggedInUsername).toBe(username); // The username should match the logged-in user
  });

  // Test for checking if the user is logged in
  it('should return true if the user is logged in', () => {
    const username = 'manav';
    const password = 'pxl';

    service.login(username, password);
    const isLoggedIn = service.isLoggedIn();

    expect(isLoggedIn).toBeTrue(); // The user should be logged in
  });

  // Test for checking if the user is not logged in
  it('should return false if the user is not logged in', () => {
    service.logout();
    const isLoggedIn = service.isLoggedIn();

    expect(isLoggedIn).toBeFalse(); // The user should not be logged in
  });

  // Test for logging out the user
  it('should log out successfully', () => {
    const username = 'sofia';
    const password = 'pxl';

    service.login(username, password); // Log in first
    service.logout();

    expect(service.getUserRole()).toBeNull(); // Role should be cleared
    expect(service.isLoggedIn()).toBeFalse(); // The user should not be logged in
  });

  // Test for checking session persistence after page reload
  it('should persist user session after page reload (simulated)', () => {
    const username = 'manav';
    const password = 'pxl';

    service.login(username, password);
    const sessionUsername = sessionStorage.getItem('username');
    const sessionRole = sessionStorage.getItem('userRole');

    expect(sessionUsername).toBe(username); // Username should be stored in sessionStorage
    expect(sessionRole).toBe('Editor'); // Role should be stored in sessionStorage

    // Simulate a new instance of the service (like page reload)
    const newService = TestBed.inject(AuthService);
    expect(newService.getUsername()).toBe(username); // The username should still be persisted
    expect(newService.getUserRole()).toBe('Editor'); // The role should still be persisted
  });
});
