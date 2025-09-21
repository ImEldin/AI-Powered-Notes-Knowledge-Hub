CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    -- Authentication fields
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    -- Profile information
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(150),
    avatar_url VARCHAR(500),
    -- Phone (for 2FA later)
    phone_number VARCHAR(20),
    phone_verified BOOLEAN DEFAULT false,
    -- Email verification
    email_verified BOOLEAN DEFAULT false,
    email_verification_token VARCHAR(255),
    email_verification_expires_at TIMESTAMP,
    -- Password reset
    password_reset_token VARCHAR(255),
    password_reset_expires_at TIMESTAMP,
    -- Account status
    is_active BOOLEAN DEFAULT true,
    is_locked BOOLEAN DEFAULT false,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    -- OAuth provider info
    provider VARCHAR(50) DEFAULT 'local',
    provider_id VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,

    CONSTRAINT unique_provider_id UNIQUE (provider, provider_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_provider ON users(provider, provider_id);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_email_verified ON users(email_verified);
CREATE INDEX idx_users_email_verification_token ON users(email_verification_token);
CREATE INDEX idx_users_password_reset_token ON users(password_reset_token);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();