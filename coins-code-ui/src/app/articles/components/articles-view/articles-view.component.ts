import { AnimationEvent } from '@angular/animations';
import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, viewChild } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, filter, map, switchMap, tap } from 'rxjs';
import { ArticlesService } from '../../services/articles.service';
import {
  articlesStateTrigger,
  loadingIndicatorStateTrigger,
  paginatorStateTrigger
} from './articles-view.component.consts';

type ArticlesState = 'loading' | 'loaded' | 'error';

@Component({
  selector: 'cc-articles-view',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    DatePipe,
    AsyncPipe
  ],
  templateUrl: './articles-view.component.html',
  styleUrl: './articles-view.component.scss',
  animations: [articlesStateTrigger, loadingIndicatorStateTrigger, paginatorStateTrigger]
})
export class ArticlesViewComponent {
  articlesState: ArticlesState = 'loading';
  showPaginator = false;
  pageSize = 0;
  totalItems = 0;
  paginator = viewChild(MatPaginator);
  articles$ = this.route.queryParams.pipe(
    tap(() => (this.articlesState = 'loading')),
    takeUntilDestroyed(),
    switchMap(params => this.articlesService.find(params)),
    tap(({ articles }) => (this.pageSize = articles.length)),
    tap(({ totalItems }) => (this.totalItems = totalItems)),
    map(({ articles }) => articles),
    tap({
      next: () => (this.articlesState = 'loaded'),
      error: () => (this.articlesState = 'error')
    })
  );
  paginatorPageChangeSub = toObservable(this.paginator)
    .pipe(
      takeUntilDestroyed(),
      filter(Boolean),
      switchMap(paginator => paginator.page),
      debounceTime(500)
    )
    .subscribe(event => this.onPageChange(event));

  constructor(
    private articlesService: ArticlesService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  onArticleStateAnimationDone(event: AnimationEvent): void {
    if (event.toState === 'loaded') {
      this.showPaginator = true;
    }
  }

  onPageChange({ pageSize, pageIndex: page }: PageEvent): void {
    this.router.navigate([], { relativeTo: this.route, queryParams: { pageSize, page } });
  }
}
