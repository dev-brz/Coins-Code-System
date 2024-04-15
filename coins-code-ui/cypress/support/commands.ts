// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

import { USERS_USER_URL } from '../../src/app/shared/configs/api.config';
import { CURRENT_USER_KEY } from '../../src/app/shared/configs/storage.config';

Cypress.Commands.add('login', (user = { username: 'TestUser' }) => {
  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username: 'username', password: 'password' }));
  cy.intercept('GET', USERS_USER_URL.replace('?1', '*'), { statusCode: 200, body: user });
});

Cypress.Commands.add('logout', () => {
  cy.clearLocalStorage(CURRENT_USER_KEY);
});
