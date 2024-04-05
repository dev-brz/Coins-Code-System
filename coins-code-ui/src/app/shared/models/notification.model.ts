type NotificationType = 'success' | 'error' | 'info' | 'warning';
export interface Notification {
  message: string;
  type: NotificationType;
}
