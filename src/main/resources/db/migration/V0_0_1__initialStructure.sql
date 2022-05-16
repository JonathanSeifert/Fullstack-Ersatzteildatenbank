-- Engine: Postgres
-- Version: 0.0.1
-- Description: Initial database structure

-- Structure
CREATE TABLE land(
  land_id CHARACTER(2) PRIMARY KEY,
  land_name VARCHAR(50) NOT NULL,
  CONSTRAINT land_format CHECK (land_id SIMILAR TO '[A-Z][A-Z]'),
  UNIQUE(land_id, land_name)
);

-- Data
INSERT INTO land(land_id, land_name) values
('DE', 'Deutschland'),
('BE', 'Belgien'),
('FR', 'Frankreich'),
('AT', 'Österreich'),
('CN', 'China'),
('US', 'Vereinigte Staaten von Amerika');

commit;