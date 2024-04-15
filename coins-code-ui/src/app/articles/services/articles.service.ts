import { HttpClient } from '@angular/common/http';
import { Injectable, Optional, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { DEFAULT_ARTICLE_IMAGE_PATH } from '../../shared/configs/assets.config';
import { ArticlesPage, ArticlesQuery } from '../models/article.model';
import { FindArticlesResponse } from '../models/http/article.http.model';
import { ArticlesHttpService } from './http/articles.http.service';

@Injectable({ providedIn: 'root' })
export class ArticlesService {
  constructor(@Optional() private articlesApi: ArticlesHttpService) {
    this.articlesApi = articlesApi ?? new ArticlesHttpService(inject(HttpClient));
  }

  find(query: ArticlesQuery): Observable<ArticlesPage> {
    return this.articlesApi.find(query).pipe(map(this.toArticlesPage));
  }

  private toArticlesPage(response: FindArticlesResponse): ArticlesPage {
    const articles = response.articles.map(responseArticle => {
      const image = responseArticle.image ?? { src: DEFAULT_ARTICLE_IMAGE_PATH, alt: $localize`default article image` };

      return { ...responseArticle, image };
    });

    return { articles, page: response.currentPage, totalItems: response.totalItems };
  }
}
