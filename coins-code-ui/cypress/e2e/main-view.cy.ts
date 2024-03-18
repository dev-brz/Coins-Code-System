import { USERS_USER_URL } from '../../src/app/shared/configs/api.config';
import {
  ACCOUNT_ROUTE,
  COINS_ROUTE,
  HISTORY_ROUTE,
  HOME_ROUTE,
  MAIN_ROUTE
} from '../../src/app/shared/configs/routes.config';
import { CURRENT_USER_KEY } from '../../src/app/shared/configs/storage.config';

describe('Main View', () => {
  beforeEach(() => {
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username: 'username', password: 'password' }));
    cy.intercept('GET', USERS_USER_URL.replace('?1', '*'), { statusCode: 200, body: { username: 'John' } });

    cy.visit(`/${MAIN_ROUTE}`);
  });

  afterEach(() => cy.clearAllLocalStorage());

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
