import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ARTICLES_URL } from '../../../shared/configs/api.config';
import { Page, PageQuery } from '../../../shared/models/page.model';
import { ArticleOverviewResult } from '../../models/http/article.http.model';

@Injectable()
export class ArticlesHttpService {
  constructor(private http: HttpClient) {}

  find(query: PageQuery): Observable<Page<ArticleOverviewResult>> {
    const params = new HttpParams({ fromObject: query });

    return this.http.get<Page<ArticleOverviewResult>>(ARTICLES_URL, { params });
  }
}
