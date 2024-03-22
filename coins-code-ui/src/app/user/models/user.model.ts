import { DEFAULT_PROFILE_IMAGE_PATH } from '../../shared/configs/assets.config';

export interface UserSettings {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface UserBase extends UserSettings {
  numberOfSends: number;
  numberOfReceives: number;
  createdAt: string;
  active: boolean;
  sendLimits: number;
}

export interface User extends UserBase {
  imageUrl: string;
}

export abstract class UserLogic {
  static hasImage(user: User): boolean {
    return user.imageUrl != DEFAULT_PROFILE_IMAGE_PATH;
  }
}
