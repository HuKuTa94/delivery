SELECT 'CREATE DATABASE delivery'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'delivery')