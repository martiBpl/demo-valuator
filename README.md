# Starting local environment

### Start local db
To be able to run/debug the application locally please follow the given steps.
    
Assuming docker environment is installed and working on your local machine.

1. From the root project folder run: `docker-compose up -d` This will start local postgres db
2. Copy `.env.dist` file to `.env` - it contains all required environmental variables configured for the docker-compose file.

To reset the db simply run `docker-compose down` and `docker-compose up -d` again.


### DB Migrations
All the required migrations will be executed on app startup. When the `MIGRATION_CONTEXTS` env variable will be set to `dev`
(configured in `.env` file) the migration will add 4 example product records into the database:

```sql
insert into
    product(id, product_id, name, price)
values
    ('115896f4-cacc-44f4-b805-18ec60784af9', 'de626924-535e-4b5b-8f41-c4fd0a01d2d1', 'Product 1', 10.50),
    ('3a04a4e4-9b2f-4e5c-bea8-1b9c10fc4bca', '56413216-c0f9-44fb-abc8-cfdd73be9bed', 'Product 2', 149),
    ('8089aa5f-78e3-4c48-bdcd-a96ffef7d9ff', 'f2e65264-5221-4911-bb2d-47f61fd87b39', 'Product 3', 1.23),
    ('23a9dd9b-1ccc-4e69-a57c-6e7dc4b7ddc2', 'cef58a7d-53e1-4ef4-a5d6-38228eb1efbe', 'Product 4', 0.01)
;
```

# Assumptions
- Prices are using max 2 decimal places,
- Service does not support multi-currency,
- Products are stored in internal database - most likely they would be synchronized from other services,
- If a given product has multiple active discounts applied the service will pick the highest possible,
- The service is calculating total amount and total discount amount based on the calculations:
```
single price after discount = original item price - item discount
total price after discount = single price after discount * quantity 
original total amount = original item price * quantity 
total discount =  original total amount - total price after discount

Prices after multiplication are rounded to 2 decimal points.
```

# Project architecture
Project is using hexagonal architecture, with 3 main modules: `domain` `app` and `infra`.
The `domain` module contains all the logic and should not be dependent on any other module.
The `infra` module is mainly for I/O operations (db repositories, external API calls, etc.) - its depending on ports 
exposed by the `domain` module, the infra module implementations can be easily switchable to any other.
The `app` module is the starting point of the application and its meant for exposing user interaction, REST API in this
example. It contains all the required configuration.