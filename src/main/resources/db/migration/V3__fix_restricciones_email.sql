ALTER TABLE cliente ALTER COLUMN email SET NOT NULL;
ALTER TABLE cliente ADD CONSTRAINT uq_cliente_email UNIQUE (email);
