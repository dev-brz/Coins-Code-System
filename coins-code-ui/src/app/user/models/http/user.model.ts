import { UserBase, UserSettings } from '../user.model';

export interface SaveUserRequestBody extends UserSettings {
  password: string;
  profileImage: File | null;
}

export interface GetUserResponseBody extends UserBase {
  imageName: string;
}
