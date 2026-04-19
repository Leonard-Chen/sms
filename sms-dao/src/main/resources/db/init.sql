SET NAMES utf8mb4;
SET SESSION sql_mode = REPLACE(REPLACE(@@SESSION.sql_mode, 'NO_ZERO_DATE', ''), 'NO_ZERO_IN_DATE', '');
SOURCE schema.sql;
SOURCE data.sql;
