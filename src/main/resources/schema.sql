  DROP TABLE IF EXISTS users, items, bookings, comments, requests;

  CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
  );

  CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(255) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requester_id BIGINT NOT NULL,
    CONSTRAINT PK_REQUEST PRIMARY KEY (id),
    CONSTRAINT FK_REQUESTER_ID FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
  );

  CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT PK_ITEM PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_OWNER FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FK_ITEM_REQUEST FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
  );

  CREATE TABLE IF NOT EXISTS bookings (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   start_date TIMESTAMP WITHOUT TIME ZONE,
   end_date TIMESTAMP WITHOUT TIME ZONE,
   status VARCHAR(20),
   item_id BIGINT NOT NULL,
   booker_id BIGINT NOT NULL,
   CONSTRAINT PK_BOOKING PRIMARY KEY (id),
   CONSTRAINT FK_BOOKING_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
   CONSTRAINT FK_BOOKING_BOOKER FOREIGN KEY (booker_id) REFERENCES users (id)
  );

  CREATE TABLE IF NOT EXISTS comments (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   text VARCHAR(2000) NOT NULL,
   item_id BIGINT NOT NULL,
   author_id BIGINT NOT NULL,
   created TIMESTAMP WITHOUT TIME ZONE,
   CONSTRAINT PK_COMMENT PRIMARY KEY (id),
   CONSTRAINT FK_COMMENT_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
   CONSTRAINT FK_COMMENT_USER FOREIGN KEY (author_id) REFERENCES users (id)
  );