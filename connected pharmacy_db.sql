DROP DATABASE IF EXISTS pharmacy_db;
CREATE DATABASE pharmacy_db;
USE pharmacy_db;

CREATE TABLE users (
    user_id     INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('Admin','Cashier') NOT NULL,
    full_name   VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
    supplier_id     INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT
);

CREATE TABLE medicines (
    medicine_id        INT PRIMARY KEY AUTO_INCREMENT,
    name               VARCHAR(150) NOT NULL,
    company            VARCHAR(100),
    medicine_type      VARCHAR(50),
    price              DECIMAL(10,2) NOT NULL,
    quantity_in_stock  INT NOT NULL DEFAULT 0,
    reorder_level      INT NOT NULL DEFAULT 10,
    expiry_date        DATE,
    supplier_id        INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
);

CREATE TABLE sales (
    sale_id       INT PRIMARY KEY AUTO_INCREMENT,
    sale_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL,
    user_id       INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE sale_items (
    sale_item_id   INT PRIMARY KEY AUTO_INCREMENT,
    sale_id        INT NOT NULL,
    medicine_id    INT NOT NULL,
    quantity_sold  INT NOT NULL,
    price_at_sale  DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
);

INSERT INTO users (username, password, role, full_name) VALUES
('admin',   'admin123', 'Admin',   'System Administrator'),
('cashier', 'cash123',  'Cashier', 'John Cashier');

INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('PharmaCorp Ltd',   'Alice Moyo',  '0111111111', 'alice@pharmacorp.co.za', '12 Main Rd, Johannesburg'),
('MediSupply SA',    'Brian Dube',  '0222222222', 'brian@medisupply.co.za', '45 Health Ave, Cape Town'),
('Wellness Distrib', 'Carol Nkosi', '0333333333', 'carol@wellness.co.za',   '78 Care St, Durban');

INSERT INTO medicines
(name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Paracetamol 500mg',  'PharmaCorp Ltd',   'Tablet',    12.50, 200, 20, '2026-12-31', 1),
('Amoxicillin 250mg',  'MediSupply SA',    'Capsule',   45.00,  15, 25, '2026-10-15', 2),
('Cough Syrup 100ml',  'Wellness Distrib', 'Syrup',     35.75,  60, 15, '2026-09-30', 3),
('Ibuprofen 400mg',    'PharmaCorp Ltd',   'Tablet',    18.00,   8, 30, '2026-11-20', 1),
('Vitamin C Injection','MediSupply SA',    'Injection', 80.00,  40, 10, '2027-01-10', 2),
('Skin Cream 50g',     'Wellness Distrib', 'Cream',     55.25,  25, 12, '2026-10-05', 3); 