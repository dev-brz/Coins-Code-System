import { HttpClient } from '@angular/common/http';
import { Injectable, Optional, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { DEFAULT_ARTICLE_IMAGE_PATH } from '../../shared/configs/assets.config';
import { Page, PageQuery } from '../../shared/models/page.model';
import { ArticleOverview } from '../models/article.model';
import { ArticleOverviewResult } from '../models/http/article.http.model';
import { ArticlesHttpService } from './http/articles.http.service';

@Injectable({ providedIn: 'root' })
export class ArticlesService {
  constructor(@Optional() private articlesApi: ArticlesHttpService) {
    this.articlesApi = articlesApi ?? new ArticlesHttpService(inject(HttpClient));
  }

  find(query: PageQuery): Observable<Page<ArticleOverview>> {
    return this.articlesApi.find(query).pipe(map(page => this.toArticlesPage(page)));
  }

  private toArticlesPage(page: Page<ArticleOverviewResult>): Page<ArticleOverview> {
    return { ...page, content: page.content.map(article => this.toArticle(article)) };
  }

  private toArticle(article: ArticleOverviewResult): ArticleOverview {
    const imageSrc = article.image ? `data:image/*;base64,${article.image}` : DEFAULT_ARTICLE_IMAGE_PATH;
    return { ...article, imageSrc };
  }
}
