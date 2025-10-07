-- normalize existing values to enum names and set default to 'LOCAL'
ALTER TABLE users ALTER COLUMN provider SET DEFAULT 'LOCAL';