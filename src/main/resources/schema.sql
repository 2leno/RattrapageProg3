CREATE TABLE IF NOT EXISTS driver (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    license_category VARCHAR(10) NOT NULL CHECK (license_category IN ('B', 'C', 'D')),
    affiliation_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle (
    id VARCHAR(255) PRIMARY KEY,
    plate_number VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('FOURGON', 'CAMION', 'BENNE')),
    capacity_tons DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS trip (
    id VARCHAR(255) PRIMARY KEY,
    driver_id VARCHAR(255) NOT NULL REFERENCES driver(id),
    vehicle_id VARCHAR(255) NOT NULL REFERENCES vehicle(id),
    trip_date DATE NOT NULL,
    departure_city VARCHAR(255) NOT NULL,
    arrival_city VARCHAR(255) NOT NULL,
    distance_km INT NOT NULL,
    billed_amount BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('COMPLETED', 'CANCELLED'))
);
