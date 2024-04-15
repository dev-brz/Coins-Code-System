import { animate, query, stagger, style, transition, trigger } from '@angular/animations';

/* prettier-ignore */
export const articlesStateTrigger = trigger('articlesState', [
  transition('* => *', [
    query(':enter', [
      style({ opacity: 0 }), 
      stagger(100, [
        animate(500, style({ opacity: 1 }))
      ])
    ], { optional: true })
  ])
]);

/* prettier-ignore */
export const loadingIndicatorStateTrigger = trigger('loadingIndicatorState', [
  transition(':leave', animate(500, style({ opacity: 0 })))
]);

/* prettier-ignore */
export const paginatorStateTrigger = trigger('paginatorState', [
  transition(':enter', [
    style({ opacity: 0 }), 
    animate(500, style({ opacity: 1 }))
  ])
]);
