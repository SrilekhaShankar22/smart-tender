-- Auto-runs on first MySQL container start
-- Creates all schemas for every microservice

CREATE DATABASE IF NOT EXISTS smart_tender_auth;
CREATE DATABASE IF NOT EXISTS smart_tender_fetch;
CREATE DATABASE IF NOT EXISTS smart_tender_processing;
CREATE DATABASE IF NOT EXISTS smart_tender_profiles;
CREATE DATABASE IF NOT EXISTS smart_tender_notifications;

GRANT ALL PRIVILEGES ON smart_tender_auth.*          TO 'tender_user'@'%';
GRANT ALL PRIVILEGES ON smart_tender_fetch.*         TO 'tender_user'@'%';
GRANT ALL PRIVILEGES ON smart_tender_processing.*    TO 'tender_user'@'%';
GRANT ALL PRIVILEGES ON smart_tender_profiles.*      TO 'tender_user'@'%';
GRANT ALL PRIVILEGES ON smart_tender_notifications.* TO 'tender_user'@'%';
FLUSH PRIVILEGES;
