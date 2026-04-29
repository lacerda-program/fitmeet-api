-- PostgreSQL Database Console - Bootcamp Project
-- This file contains SQL commands to test and explore your database

-- 1. Check database connection and version
SELECT version();
SELECT current_database();
SELECT current_user;

-- 2. List all tables in the database
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- 3. Check if tables have data (after Spring Boot runs migrations)
-- Users table
SELECT COUNT(*) as users_count FROM users;

-- Activity Types (should have 5 pre-loaded types)
SELECT COUNT(*) as activity_types_count FROM activity_types;
SELECT id, name, description FROM activity_types ORDER BY name;

-- Activities table
SELECT COUNT(*) as activities_count FROM activities;

-- 4. View table structures
-- Users table structure
\d users;

-- Activity Types table structure
\d activity_types;

-- Activities table structure
\d activities;

-- 5. Sample queries to test relationships
-- Get all activities with their types
SELECT a.id, a.title, a.description, at.name as activity_type
FROM activities a
JOIN activity_types at ON a.type_id = at.id
ORDER BY a.created_at DESC;

-- Get users with their XP and level
SELECT id, name, email, xp, level
FROM users
ORDER BY xp DESC, level DESC;

-- 6. Check foreign key constraints
SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM
    information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
      AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
      AND ccu.table_schema = tc.table_schema
WHERE
    tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_schema = 'public'
ORDER BY tc.table_name;

-- 7. Check indexes
SELECT
    tablename,
    indexname,
    indexdef
FROM
    pg_indexes
WHERE
    schemaname = 'public'
ORDER BY tablename, indexname;

-- 8. Sample INSERT for testing (optional - Spring Boot handles this)
-- Note: These would normally be handled by your application, not manual SQL

-- 9. Check database size and statistics
SELECT
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats
WHERE schemaname = 'public'
ORDER BY tablename, attname;

-- 10. Useful maintenance queries
-- Show running queries (if any)
SELECT pid, age(clock_timestamp(), query_start), usename, query
FROM pg_stat_activity
WHERE query != '<IDLE>' AND query NOT ILIKE '%pg_stat_activity%'
ORDER BY query_start DESC;

-- Show table sizes
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
