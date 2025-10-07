-- Make password field nullable
ALTER TABLE "users" ALTER COLUMN password DROP NOT NULL;