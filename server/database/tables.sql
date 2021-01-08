-- MINIMAL DATABASE
-- TODO: split sensitive data from user table to dedicated tables
USE authdb;

DROP TABLE IF EXISTS auth_user;
DROP TABLE IF EXISTS auth_session;

CREATE TABLE auth_user (
    user_id VARCHAR(50) NOT NULL,
    name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
	biometric_histo_R VARCHAR(256) NOT NULL,
	biometric_histo_G VARCHAR(256) NOT NULL,
	biometric_histo_B VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    PRIMARY KEY (user_id)
);

-- TODO: change and manage sessions programmatically
CREATE TABLE auth_session (
    token VARCHAR(256) NOT NULL,
    user_id VARCHAR(50)  NOT NULL,
    PRIMARY KEY(token),
    FOREIGN KEY (user_id) REFERENCES auth_user(user_id)
);

INSERT INTO(
    "azerty",
    "toto",
    "tutu",
    "123",
    "123",
    "123",
    "123"
) VALUES auth_user;