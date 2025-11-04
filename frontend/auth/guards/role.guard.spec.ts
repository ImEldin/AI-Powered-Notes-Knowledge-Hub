import { TestBed } from '@angular/core/testing';
import { RoleGuard } from './role.guard';

describe('RoleGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(RoleGuard).toBeTruthy();
  });
});
