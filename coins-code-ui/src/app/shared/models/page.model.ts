export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export type PageQuery = Partial<{
  page: number;
  size: number;
}>;
