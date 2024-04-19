import { ArticleOverview } from '../../articles/models/article.model';
import { Page } from '../models/page.model';

interface Options {
  total?: number;
}

export function prepareSampleArticlesPage({ total }: Required<Options> = { total: 3 }): Page<ArticleOverview> {
  return {
    content: prepareSampleArticles({ total }),
    number: 0,
    size: total,
    totalElements: total,
    totalPages: 1
  };
}

export function prepareSampleArticles({ total }: Required<Options> = { total: 3 }): ArticleOverview[] {
  return Array(total)
    .fill(0)
    .map(
      (_, i): ArticleOverview => ({
        id: i,
        title: 'title',
        description: 'description',
        createdAt: new Date().toISOString(),
        imageSrc: ''
      })
    );
}
