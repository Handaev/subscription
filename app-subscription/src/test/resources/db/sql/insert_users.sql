INSERT INTO users (name, subscription_type, end_date)
VALUES ('user_for_edit', 'FREE', NULL);

INSERT INTO users (name, subscription_type, end_date)
VALUES ('free_user_error', 'FREE', NULL);

INSERT INTO users (name, subscription_type, end_date)
VALUES ('paid_user_renew', 'PAID', CURRENT_DATE + INTERVAL '10 day');