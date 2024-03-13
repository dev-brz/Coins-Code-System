import { LOGIN_URL, USERS_PROFILE_IMAGE_URL, USERS_USER_URL } from '../../src/app/shared/configs/api.config';

describe('Register View', () => {
  beforeEach(() => {
    cy.visit('/register');
    cy.intercept(USERS_USER_URL.replace('?1', '*'), { statusCode: 404 });
    cy.intercept(LOGIN_URL.replace('?1', '*'), { statusCode: 200 });
    cy.intercept(USERS_PROFILE_IMAGE_URL.replace('?1', '*'), { statusCode: 200 });
  });

  it('Should contain form and login button disabled', () => {
    cy.get('form').should('exist');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('Should enable submission button if form is filled', () => {
    cy.get('input[formcontrolname="username"]').type('username', { force: true });
    cy.get('input[formcontrolname="firstName"]').type('first name', { force: true });
    cy.get('input[formcontrolname="lastName"]').type('last name', { force: true });
    cy.get('input[formcontrolname="email"]').type('email@mail.com', { force: true });
    cy.get('input[formcontrolname="password"]').type('password', { force: true });
    cy.get('input[formcontrolname="passwordRepeat"]').type('password', { force: true });
    cy.get('button[type="submit"]').should('be.enabled');
  });
});
