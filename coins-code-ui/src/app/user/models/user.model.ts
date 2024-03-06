export interface UserBase {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  profileImage: File | string | null;
}
