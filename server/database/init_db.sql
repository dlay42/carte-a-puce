CREATE DATABASE authdb;
CREATE USER 'authsrv'@'localhost' IDENTIFIED BY 'azerty';
GRANT ALL PRIVILEGES ON authdb.* TO 'authsrv'@'localhost';
FLUSH PRIVILEGES;