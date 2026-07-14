CREATE TABLE message_status (
    id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWSEQUENTIALID(),
    message_id UNIQUEIDENTIFIER NOT NULL,
    recipient_id UNIQUEIDENTIFIER NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'SENT',
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_message_status PRIMARY KEY CLUSTERED (id),
    CONSTRAINT uq_message_status UNIQUE (message_id, recipient_id),
    CONSTRAINT fk_ms_message FOREIGN KEY (message_id)
        REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT ck_ms_status CHECK (status IN ('SENT', 'DELIVERED', 'READ'))
);

CREATE NONCLUSTERED INDEX ix_ms_recipient_status
    ON message_status (recipient_id, status)
    INCLUDE (message_id);

CREATE NONCLUSTERED INDEX ix_ms_message_id
    ON message_status (message_id)
    INCLUDE (recipient_id, status);
