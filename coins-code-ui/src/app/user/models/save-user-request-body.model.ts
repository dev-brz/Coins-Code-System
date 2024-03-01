import { UserBase } from './user.model';

export interface SaveUserRequestBody extends UserBase {
  password: string;
}
