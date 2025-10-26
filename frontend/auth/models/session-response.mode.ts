export interface SessionResponse {
  sessionId: string;
  device: string;
  createdAt: string;
  expiresAt: string;
  current: boolean;
}
