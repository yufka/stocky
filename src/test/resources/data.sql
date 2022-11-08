INSERT INTO stocky_test.subscription_plan
(name, call_count, call_time_limit_millis, stock_count, stock_time_limit_millis)

VALUES
    ('DEMO', 1000, 2629800000 , 10, 2629800000),
    ('SILVER', 1, 60000, 100, 2629800000),
    ('GOLD', 1, 10000, NULL, NULL);

-- ADMIN USER THAT IS ALLOWED TO CREATE OTHER USERS
INSERT INTO stocky_test.api_user(name, password, subscription_id, superuser)
VALUES ('admin', 'admin', 1, TRUE);
