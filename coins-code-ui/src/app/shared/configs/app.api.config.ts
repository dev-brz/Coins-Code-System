import { api } from '../../../environment/environment.dev';

export const LOGIN_URL = `${api}/users/login`;
export const LOGOUT_URL = `${api}/users/logout`;

export const NO_AUTH_URLS = [LOGIN_URL];
