import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, asyncScheduler, of } from 'rxjs';
import { FindArticlesQuery, FindArticlesResponse } from '../../models/http/article.http.model';

@Injectable()
export class ArticlesHttpService {
  constructor(private http: HttpClient) {}

  find(query: FindArticlesQuery): Observable<FindArticlesResponse> {
    const image = (): FindArticlesResponse['articles'][0]['image'] => ({
      src: `https://picsum.photos/400/250?q=${Math.random()}`,
      alt: 'idk'
    });
    const articles = new Array(parseInt(`${query.pageSize ?? 10}`)).fill(0).map(() => ({
      id: Math.floor(9999999999 * Math.random()),
      image: Math.random() > 0.1 ? image() : undefined,
      createdDate: new Date().toISOString(),
      title: this.randomText(3),
      content: this.randomText(30)
    }));

    return of({ articles, currentPage: query.page ?? 0, totalItems: 85 }, asyncScheduler);
  }

  private randomText(len: number): string {
    return Array(len)
      .fill(0)
      .map(() => randomWordsList[Math.floor(Math.random() * randomWordsList.length)])
      .join(' ');
  }
}

const randomWordsList = `offspring
camera
coerce
efflux
feature
response
play
bay
poison
option
adviser
judicial
farewell
remain
remunerate
undermine
gradual
package
medicine
pudding
flourish
plain
accessible
will
clinic
muscle
last
salad
reasonable
curve
connection
freight
prize
delete
grave
experienced
merit
false
faint
mutual
arise
wealth
improve
confuse
motivation
use
good
bottom
environment
proposal`
  .split('\n')
  .map(w => w.trim());
