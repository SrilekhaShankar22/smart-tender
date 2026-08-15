export interface RegisterRequest { firstName: string; lastName: string; email: string; password: string; }
export interface LoginRequest { email: string; password: string; }
export interface AuthResponse { accessToken: string; refreshToken: string; tokenType: string; expiresIn: number; user: UserResponse; }
export interface UserResponse { id: number; email: string; firstName: string; lastName: string; enabled: boolean; roles: string[]; }
