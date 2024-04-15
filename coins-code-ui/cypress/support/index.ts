/* eslint-disable @typescript-eslint/no-namespace */
import type { GetUserResponseBody } from '../../src/app/user/models/http/user.model';

declare global {
  namespace Cypress {
    interface Chainable {
      login(user?: Partial<GetUserResponseBody>): Chainable<null>;
      logout(): Chainable<null>;
    }
  }
}
