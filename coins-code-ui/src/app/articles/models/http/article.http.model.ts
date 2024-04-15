import { ArticlesQuery } from '../article.model';

export type FindArticlesQuery = ArticlesQuery;

export interface FindArticlesResponse {
  articles: FoundArticle[];
  currentPage: number;
  totalItems: number;
}

export interface FoundArticle {
  id: number;
  title: string;
  content: string;
  createdDate: string;
  image?: {
    src: string;
    alt: string;
  };
}
