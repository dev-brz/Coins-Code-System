import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export class FileServiceError extends Error {}

@Injectable({ providedIn: 'root' })
export class FileService {
  static readonly MAX_IMAGE_SIZE_BYTES = 1_048_576;

  selectImage(): Observable<File> {
    return new Observable(subscriber => {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = 'image/*';

      input.oncancel = (): void => subscriber.complete();
      input.oninput = (): void => {
        const files = input.files!;
        if (files.length != 1) {
          subscriber.error(new FileServiceError(`Expected single image but got ${files.length} of them`));
          return;
        }

        const file = files[0];
        if (file.size > FileService.MAX_IMAGE_SIZE_BYTES) {
          subscriber.error(new FileServiceError(`File exceeds ${FileService.MAX_IMAGE_SIZE_BYTES} bytes limit`));
          return;
        }

        subscriber.next(file);
        subscriber.complete();
      };

      input.click();
    });
  }
}
