-- Run by postgres-init of docker-compose.yaml on every `up`, so it is idempotent

-- Statistics of every SQL statement (the server preloads the library, see the postgres command in docker-compose.yaml)
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- postgres-exporter connects as a role of its own: pg_monitor lets it read the statistics only, and its own
-- statements are left out of the exported pg_stat_statements (--collector.stat_statements.exclude_users)
DO $$
BEGIN
    CREATE ROLE exporter LOGIN PASSWORD 'exporter';
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

GRANT pg_monitor TO exporter;
