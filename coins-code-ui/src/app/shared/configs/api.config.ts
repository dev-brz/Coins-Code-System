import environment from '../../../environment/environment';

export const USERS_URL = `${environment.apiUrl}/users`;
export const LOGIN_URL = `${environment.apiUrl}/users/login`;
export const LOGOUT_URL = `${environment.apiUrl}/users/logout`;
export const USERS_USER_URL = `${environment.apiUrl}/users/?1`;
export const USERS_PROFILE_IMAGE_URL = `${environment.apiUrl}/users/image`;

export const NO_AUTH_URLS = [LOGIN_URL];
