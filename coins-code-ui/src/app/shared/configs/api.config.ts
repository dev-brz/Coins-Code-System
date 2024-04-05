import environment from '../../../environment/environment';

export const USERS_URL = `${environment.apiUrl}/users`;
export const LOGIN_URL = `${environment.apiUrl}/users/login`;
export const LOGOUT_URL = `${environment.apiUrl}/users/logout`;
export const USERS_USER_URL = `${environment.apiUrl}/users/?1`;
export const USERS_PASSWORD_URL = `${environment.apiUrl}/users/password`;
export const USERS_PROFILE_IMAGE_URL = `${environment.apiUrl}/users/image`;
export const COINS_URL = `${environment.apiUrl}/coins`;
export const COINS_COIN_URL = `${environment.apiUrl}/coins/?1`;
export const COINS_COIN_IMAGE_URL = `${environment.apiUrl}/coins/?1/image`;
export const TRANSACTION_URL = `${environment.apiUrl}/transactions`;

export const TRANSACTION_TOP_UP_URL = `${environment.apiUrl}/transactions/top-up`;

export const NO_AUTH_URLS = [LOGIN_URL];
export const NO_AUTH_POST_URLS = [USERS_URL];
