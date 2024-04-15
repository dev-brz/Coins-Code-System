import { COINS_ROUTE, CREATE_COIN_ROUTE, MAIN_ROUTE } from '../../src/app/shared/configs/routes.config';
import { CURRENT_USER_KEY } from '../../src/app/shared/configs/storage.config';
import { COINS_COIN_IMAGE_URL, COINS_URL, USERS_USER_URL } from '../../src/app/shared/configs/api.config';

const res = {
  statusCode: 200,
  body: {
    coins: [
      {
        uid: '7fe9ec2a-efab-4bab-abfd-e8e8cd1e1adc',
        name: 'kirawade',
        imageName: 'imageName',
        description: 'kirimonsher',
        amount: 100.0
      },
      {
        uid: 'e270c694-fe4a-4336-b9e7-fd9537dbc992',
        name: 'Test',
        imageName: 'imageName',
        description: 'Point O',
        amount: 1000.0
      }
    ]
  }
};

describe('Coins View', () => {
  beforeEach(() => {
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username: 'username', password: 'password' }));
    cy.intercept('GET', COINS_URL + '?username=username', res);
    cy.intercept('GET', COINS_COIN_IMAGE_URL.replace('?1', 'imageName'), { statusCode: 200, body: 'image' });
    cy.intercept('GET', USERS_USER_URL.replace('?1', 'username'), {
      statusCode: 200,
      body: { username: 'username' }
    });
    cy.visit(`/${MAIN_ROUTE}/${COINS_ROUTE}`);
  });

  afterEach(() => cy.clearAllLocalStorage());

  context('Desktop', () => {
    beforeEach(() => {
      cy.viewport(1200, 800);
    });

    it('Should open coins view', () => {
      cy.url().should('contain', `/${COINS_ROUTE}`);
      cy.get('cc-coin').should('have.length', 2);
    });

    it('Should open create coin view', () => {
      cy.get('button[aria-label="Add new coin"]').click();
      cy.get('mat-dialog-container').should('be.visible');
    });
  });

  context('Mobile context', () => {
    beforeEach(() => {
      cy.viewport(380, 500);
    });

    it('Should have routing working', () => {
      cy.get('a[ng-reflect-router-link="create"]').click();
      cy.url().should('contain', `/${CREATE_COIN_ROUTE}`);
    });
  });
});
