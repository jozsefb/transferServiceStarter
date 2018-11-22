# Transfer Service

Handles account creation, deposits and transfers between accounts.
Using Vert.x for a simple web server and api routing.

## Build

```
gradle clean build
```

## Run

### Using an IDE

Execute *transfers/src/main/java/com/rev/test/transfers/Runner#main()*

### Using the command line

after building the project
```bash
java -jar * transfers/build/libs/transfers-1.0-SNAPSHOT-fat.jar
```


##API Endpoints

Using json format for data transfer.

* Status:

    GET http://localhost:8080/status - to check that the app is healthy

* create a new account:

    POST http://localhost:8080/api/account
    ```
    request: {
        "userName":"some user",
        "currencyCode": "GBP",
        "depositAmount": 0
    }
    ```

    decided to link registration, account creation, and first deposit
    together as this is not the focus of the test, but I do need some
    data to test with. userName is unique within the system.

    ```
    response: {
                  "accountId": "ff9e5e04-73be-4d1c-9238-58fd13d6d34c",
                  "userId": "c0801f77-4d7d-4bae-ad5b-01b4b8dfb827",
                  "status": "ACTIVE",
                  "wallets": [
                      {
                          "balanceType": "DEBIT",
                          "currency": "GBP",
                          "amount": 0
                      }
                  ],
                  "description": null
              }
    ```

* get an account / accounts:

    GET http://localhost:8080/api/account - returns all accounts

    GET http://localhost:8080/api/account/{accountId}

    ```
    response: {
                  "accountId": "ff9e5e04-73be-4d1c-9238-58fd13d6d34c",
                  "userId": "c0801f77-4d7d-4bae-ad5b-01b4b8dfb827",
                  "status": "ACTIVE",
                  "wallets": [
                      {
                          "balanceType": "DEBIT",
                          "currency": "GBP",
                          "amount": 100
                      }
                  ],
                  "description": null
              }
    ```

* deposit:

    POST http://localhost:8080/api/deposit

    ```
    request: {
        "targetAccountId": "ff9e5e04-73be-4d1c-9238-58fd13d6d34c",
        "currencyCode": "GBP",
        "depositAmount": 100
    }
    ```

    ```
    response: {
                  "accountId": "ff9e5e04-73be-4d1c-9238-58fd13d6d34c",
                  "userId": "c0801f77-4d7d-4bae-ad5b-01b4b8dfb827",
                  "status": "ACTIVE",
                  "wallets": [
                      {
                          "balanceType": "DEBIT",
                          "currency": "GBP",
                          "amount": 100
                      }
                  ],
                  "description": null
              }
    ```

* transfer:

    POST http://localhost:8080/api/transfer


    ```
    request: {
        "sourceAccountId": "c74146a1-77cc-4390-88eb-13f0e6890a6c",
        "targetAccountId": "703fddf5-724d-4bd9-bece-926cfd930a95",
        "currencyCode": "GBP",
        "amount": 100
    }
    ```

    ```
    response: {
        "sourceAccount": {
            "accountId": "c74146a1-77cc-4390-88eb-13f0e6890a6c",
            "userId": "4990b6df-876c-482d-8263-46bfada88aed",
            "status": "ACTIVE",
            "wallets": [
                {
                    "balanceType": "DEBIT",
                    "currency": "GBP",
                    "amount": 9900
                }
            ],
            "description": null
        },
        "destinationAccount": {
            "accountId": "703fddf5-724d-4bd9-bece-926cfd930a95",
            "userId": "26fdd0ce-0340-479b-b536-8b36bac62015",
            "status": "ACTIVE",
            "wallets": [
                {
                    "balanceType": "DEBIT",
                    "currency": "GBP",
                    "amount": 10100
                }
            ],
            "description": null
        }
    }
    ```