interface MockFileOptions {
  filename: string;
  sizeInBytes: number;
}

export function reSpyOnProperty<T, K extends keyof T>(object: T, property: K): jasmine.Spy<() => T[K]> {
  return Object.getOwnPropertyDescriptor(object, property)!.get as jasmine.Spy<() => T[K]>;
}

export function createMockFileList(totalFiles: number, options?: Partial<MockFileOptions>): FileList {
  const files = Array(totalFiles)
    .fill(0)
    .map(() => {
      const file = new File(['content'], options?.filename ?? 'dummy.txt');

      if (options?.sizeInBytes != undefined) {
        spyOnProperty(file, 'size').and.returnValue(options.sizeInBytes);
      }

      return file;
    });

  return Object.assign(files, { item: (index: number) => files.at(index) ?? null });
}

export function failDone(done: DoneFn): void {
  fail();
  done();
}
