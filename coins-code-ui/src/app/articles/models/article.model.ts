export interface Article {
  id: number;
  title: string;
  content: string;
  createdDate: string;
  image: {
    src: string;
    alt: string;
  };
}

export interface ArticlesPage {
  articles: Article[];
  page: number;
  totalItems: number;
}

export type ArticlesQuery = Partial<{
  page: number;
  pageSize: number;
}>;
