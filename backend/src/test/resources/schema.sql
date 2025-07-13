CREATE TABLE IF NOT EXISTS country (
  country_id SERIAL PRIMARY KEY,
  name VARCHAR(100),
  code VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS city (
  city_id SERIAL PRIMARY KEY,
  name VARCHAR(100),
  country_id INT REFERENCES country(country_id)
);
