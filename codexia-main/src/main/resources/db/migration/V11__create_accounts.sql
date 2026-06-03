CREATE TABLE accounts (
                          id           UUID         NOT NULL,
                          email        VARCHAR(255) NOT NULL,
                          name         VARCHAR(20)  NOT NULL,
                          avatar_url   VARCHAR(500),
                          role         VARCHAR(20)  NOT NULL DEFAULT 'USER',
                          created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
                          deleted_at   TIMESTAMP WITH TIME ZONE,

                          CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_accounts_email
    ON accounts (email);