import { TestBed } from '@angular/core/testing';

import { createMockFileList, failDone } from '../utils/test-utils';
import { FileService, FileServiceError } from './file.service';

describe('FileService', () => {
  let service: FileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should complete on cancel', done => {
    // GIVEN
    const input = document.createElement('input');
    spyOn(document, 'createElement').and.returnValue(input);
    spyOn(input, 'click').and.callFake(() => input.dispatchEvent(new Event('cancel')));

    // WHEN
    service.selectImage().subscribe({
      // THEN
      complete: () => done(),
      next: () => failDone(done),
      error: () => failDone(done)
    });
  });

  it('should throw on zero files', done => {
    // GIVEN
    const input = document.createElement('input');
    spyOn(document, 'createElement').and.returnValue(input);
    spyOn(input, 'click').and.callFake(() => input.dispatchEvent(new Event('input')));
    spyOnProperty(input, 'files').and.returnValue(createMockFileList(0));

    // WHEN
    service.selectImage().subscribe({
      // THEN
      next: () => failDone(done),
      error: err => {
        expect(err).toBeInstanceOf(FileServiceError);
        done();
      }
    });
  });

  it('should throw on multiple files', done => {
    // GIVEN
    const input = document.createElement('input');
    spyOn(document, 'createElement').and.returnValue(input);
    spyOn(input, 'click').and.callFake(() => input.dispatchEvent(new Event('input')));
    spyOnProperty(input, 'files').and.returnValue(createMockFileList(2));

    // WHEN
    service.selectImage().subscribe({
      // THEN
      next: () => failDone(done),
      error: err => {
        expect(err).toBeInstanceOf(FileServiceError);
        done();
      }
    });
  });

  it('should throw on file too large', done => {
    // GIVEN
    const input = document.createElement('input');
    spyOn(document, 'createElement').and.returnValue(input);
    spyOn(input, 'click').and.callFake(() => input.dispatchEvent(new Event('input')));
    spyOnProperty(input, 'files').and.returnValue(
      createMockFileList(1, { sizeInBytes: FileService.MAX_IMAGE_SIZE_BYTES + 1 })
    );

    // WHEN
    service.selectImage().subscribe({
      // THEN
      next: () => failDone(done),
      error: err => {
        expect(err).toBeInstanceOf(FileServiceError);
        done();
      }
    });
  });

  it('should emit on single file', done => {
    // GIVEN
    const input = document.createElement('input');
    spyOn(document, 'createElement').and.returnValue(input);
    spyOn(input, 'click').and.callFake(() => input.dispatchEvent(new Event('input')));
    spyOnProperty(input, 'files').and.returnValue(createMockFileList(1, { filename: 'dummy.txt' }));

    // WHEN
    service.selectImage().subscribe({
      // THEN
      next: file => {
        expect(file).not.toBeNull();
        expect(file.name).toEqual('dummy.txt');
        done();
      },
      error: () => failDone(done)
    });
  });
});
