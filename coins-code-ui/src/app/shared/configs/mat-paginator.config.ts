import { MatPaginatorIntl } from '@angular/material/paginator';

export const matPaginatorIntlFactory = (): MatPaginatorIntl => {
  const intl = new MatPaginatorIntl();

  intl.previousPageLabel = $localize`Previous page`;
  intl.nextPageLabel = $localize`Next page`;
  intl.firstPageLabel = $localize`First page`;
  intl.lastPageLabel = $localize`Last page`;
  intl.itemsPerPageLabel = `${$localize`Items per page`}:`;
  intl.getRangeLabel = rangeLabelFn;

  return intl;
};

const rangeLabelFn = (page: number, pageSize: number, length: number): string => {
  const of = $localize`of`;
  if (length === 0 || pageSize === 0) {
    return `0 ${of} ${length}`;
  }

  length = Math.max(length, 0);
  const startIndex = page * pageSize;
  const endIndex = startIndex < length ? Math.min(startIndex + pageSize, length) : startIndex + pageSize;

  return `${startIndex + 1} - ${endIndex} ${of} ${length}`;
};
