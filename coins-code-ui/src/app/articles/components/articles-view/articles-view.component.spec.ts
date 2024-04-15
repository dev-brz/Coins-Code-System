import { ComponentFixture, TestBed, fakeAsync, flush } from '@angular/core/testing';

import { PageEvent } from '@angular/material/paginator';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { EMPTY, Subject, firstValueFrom, of, tap } from 'rxjs';
import { Article } from '../../models/article.model';
import { ArticlesService } from '../../services/articles.service';
import { ArticlesViewComponent } from './articles-view.component';

describe('ArticlesViewComponent', () => {
  let component: ArticlesViewComponent;
  let fixture: ComponentFixture<ArticlesViewComponent>;
  let routerMock: jasmine.SpyObj<Router>;
  let activatedRouteMock: jasmine.SpyObj<ActivatedRoute>;
  let articlesServiceMock: jasmine.SpyObj<ArticlesService>;
  let queryParamsSubject: Subject<Params>;

  beforeEach(async () => {
    queryParamsSubject = new Subject<Params>();
    routerMock = jasmine.createSpyObj<Router>(['navigate']);
    activatedRouteMock = jasmine.createSpyObj<ActivatedRoute>([], { queryParams: queryParamsSubject });
    articlesServiceMock = jasmine.createSpyObj<ArticlesService>(['find']);

    await TestBed.configureTestingModule({
      imports: [ArticlesViewComponent, NoopAnimationsModule],
      providers: [
        { provide: ArticlesService, useValue: articlesServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticlesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render articles on page load', () => {
    // GIVEN
    articlesServiceMock.find.and.returnValue(of({ articles: prepareSampleArticles(3), page: 0, totalItems: 3 }));

    // WHEN
    queryParamsSubject.next({});
    fixture.detectChanges();
    const articles = fixture.debugElement.queryAll(By.css('article'));

    // THEN
    expect(articles).toHaveSize(3);
  });

  it('should fetch articles with proper query on page change', fakeAsync(() => {
    // GIVEN
    const pageIndex = 5;
    articlesServiceMock.find.and.returnValue(EMPTY);
    routerMock.navigate.and.callFake((_, extras) => {
      const { page } = extras!.queryParams!;
      return firstValueFrom(of(true).pipe(tap(() => queryParamsSubject.next({ page }))));
    });

    // WHEN
    component.onPageChange({ pageIndex } as PageEvent);
    flush();

    // THEN
    expect(articlesServiceMock.find).toHaveBeenCalledWith({ page: pageIndex });
  }));

  function prepareSampleArticles(total: number = 3): Article[] {
    return Array(total)
      .fill(0)
      .map(
        (_, i): Article => ({
          id: i,
          title: 'title',
          content: 'content',
          createdDate: new Date().toISOString(),
          image: { src: '', alt: '' }
        })
      );
  }
});
