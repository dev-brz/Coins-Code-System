import { ARTICLES_URL } from '../../src/app/shared/configs/api.config';
import { ARTICLES_ROUTE, MAIN_ROUTE } from '../../src/app/shared/configs/routes.config';
import { prepareSampleArticlesPage } from '../../src/app/shared/utils/test-sample-utils';

describe('Articles View', () => {
  beforeEach(() => {
    cy.login();
    cy.intercept(`${ARTICLES_URL}*`, req => {
      const total = parseInt(`${req.query['size'] ?? '10'}`);
      req.reply({ statusCode: 200, body: prepareSampleArticlesPage({ total }) });
    });
    cy.visit(`/${MAIN_ROUTE}/${ARTICLES_ROUTE}`);
  });

  afterEach(() => {
    cy.logout();
  });

  it('Should contain articles', () => {
    cy.get('article').should('exist').and('have.length', 10);
  });

  it('Should contain paginator', () => {
    cy.get('.paginator').should('exist');
  });

  it('Should change articles on paginator page size change', () => {
    cy.get('.paginator mat-select').click();
    cy.get('mat-option:first-of-type()').click();
    cy.get('article').should('exist').and('have.length', 5);
  });
});
