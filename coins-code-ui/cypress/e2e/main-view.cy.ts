import {
  ACCOUNT_ROUTE,
  COINS_ROUTE,
  HISTORY_ROUTE,
  HOME_ROUTE,
  MAIN_ROUTE
} from '../../src/app/shared/configs/routes.config';

describe('Main View', () => {
  beforeEach(() => {
    cy.login();
    cy.visit(`/${MAIN_ROUTE}`);
  });

  afterEach(() => cy.logout());

  it('Should open main view', () => {
    cy.url().should('contain', `/${MAIN_ROUTE}`);
  });

  it('Should have routing working', () => {
    cy.get('a[ng-reflect-router-link="home"]').click();
    cy.url().should('contain', `/${HOME_ROUTE}`);
    cy.get('a[ng-reflect-router-link="coins"]').click();
    cy.url().should('contain', `/${COINS_ROUTE}`);
    cy.get('a[ng-reflect-router-link="history"]').click();
    cy.url().should('contain', `/${HISTORY_ROUTE}`);
    cy.get('img.avatar').click();
    cy.get('a[ng-reflect-router-link="account"]').click();
    cy.url().should('contain', `/${ACCOUNT_ROUTE}`);
  });
});
