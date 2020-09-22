DROP TABLE IF EXISTS product;

CREATE TABLE product (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  product_Id VARCHAR(25) NOT NULL,
  title VARCHAR(250) NOT NULL,
  description VARCHAR(250) NOT NULL,
  brand VARCHAR(25) NOT NULL,
  price DECIMAL(20, 2) NOT NULL,
  color VARCHAR(25) NOT NULL
);

INSERT INTO product (product_Id, title, description, brand, price, color) VALUES
  ('GAS1234567', 'Jeans', 'Slim fit jeans', 'GAS', 100, 'Blue'),
  ('REP7876543', 'Jeans', 'Straight fit jeans', 'REPLAY', 150, 'Light Blue'),
  ('BOS9987676', 'Shirt', 'Button Down Oxford', 'BOSS', 120, 'Navy Blue'),
  ('LEV8763928', 'T-Shirt', 'Color Block', 'LEVIS', 100, 'Pearl White'),
  ('GAS8745129', 'Joggers', 'Plain', 'GAS', 100, 'Dark Gray');