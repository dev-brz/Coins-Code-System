import { LOGIN_URL } from '../../src/app/shared/configs/app.api.config';
describe('Login View', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.intercept(LOGIN_URL, { statusCode: 401 });
  });

  it('Should contain form and login button disabled', () => {
    cy.get('form').should('exist');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('Should enable submission button if form is filled', () => {
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formcontrolname="username"]').type('username', {
      force: true
    });
    cy.get('input[formcontrolname="password"]').type('password', {
      force: true
    });
    cy.get('button[type="submit"]').should('be.enabled');
  });
});
