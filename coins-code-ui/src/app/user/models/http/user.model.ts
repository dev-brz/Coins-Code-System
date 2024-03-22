import { RequireOnly } from '../../../shared/types/require-some.type';
import { UserBase, UserSettings } from '../user.model';

export interface SaveUserRequestBody extends UserSettings {
  password: string;
  profileImage: File | null;
}

export type UpdateUserRequestBody = RequireOnly<UserSettings, 'username'>;

export interface UpdatePasswordRequestBody {
  oldPassword: string;
  newPassword: string;
}

export interface GetUserResponseBody extends UserBase {
  imageName: string;
}
