CREATE TABLE IF NOT EXISTS accounts (
     id BIGSERIAL PRIMARY KEY,
     customer_id BIGINT NOT NULL,
     account_number VARCHAR(20) UNIQUE NOT NULL,
     account_type VARCHAR(20) NOT NULL,
     balance DECIMAL(15, 2) DEFAULT 0.00,
     currency VARCHAR(3) DEFAULT 'USD',
     status VARCHAR(20) DEFAULT 'ACTIVE',
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 1. INDICE: Acelerar las busquedas de cuentas por cliente
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);

-- 2. TRIGGER: Actualizar automaticamente la columna updated_at
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_accounts_modtime
BEFORE UPDATE ON accounts
FOR EACH ROW 
EXECUTE FUNCTION update_modified_column();

-- 3. STORED PROCEDURE: Transferir dinero de forma atómica y segura
CREATE OR REPLACE PROCEDURE sp_process_transfer(
     p_from_account_id BIGINT,
     p_to_account_id BIGINT,
     p_amount DECIMAL
)
LANGUAGE plpgsql
AS $$
BEGIN 
     -- Validar saldo suficiente
     IF (SELECT balance FROM accounts WHERE id = p_from_account_id) < p_amount THEN
          RAISE EXCEPTION 'Saldo insuficiente en la cuenta de origen';
     END IF;

     -- Restar a la cuenta origen 
     UPDATE accounts 
     SET balance = balance - p_amount
     WHERE id = p_from_account_id;

     -- Sumar a la cuenta destino
     UPDATE accounts
     SET balance = balance + p_amount
     WHERE id = p_to_account_id;
END;
$$;
