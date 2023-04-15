import { TestBed } from '@angular/core/testing';

import { GraphActionsService } from './graph-actions.service';

describe('GraphActionsService', () => {
  let service: GraphActionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GraphActionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
