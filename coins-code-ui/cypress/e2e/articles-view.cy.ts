import { ARTICLES_ROUTE, MAIN_ROUTE } from '../../src/app/shared/configs/routes.config';

describe('Articles View', () => {
  beforeEach(() => {
    cy.login();
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
