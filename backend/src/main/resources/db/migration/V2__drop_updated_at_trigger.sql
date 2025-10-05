-- Drop the trigger and function that conflicts with Java @PreUpdate
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Add a role attribute to user table
ALTER TABLE users
ADD COLUMN role VARCHAR(50) DEFAULT 'USER'
CHECK (role IN ('USER', 'ADMIN'));
