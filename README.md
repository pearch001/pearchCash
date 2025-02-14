Here's a **README.md** file for your multicurrency wallet API project:

```markdown
# Multi-Currency Wallet API

A RESTful API for managing multicurrency wallets, built with **Spring Boot**, **JPA/Hibernate**, and **PostgreSQL**. Users can create accounts, deposit/withdraw funds, transfer money, and view transactions.

## Features
- ✅ User registration and JWT-based authentication
- ✅ Create accounts in multiple currencies (e.g., USD, EUR)
- ✅ Deposit/withdraw funds
- ✅ Transfer money between users
- ✅ View account balance and transaction history
- 🔒 Secure endpoints with JWT
- 🧪 Unit and integration tests

## Technologies
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Security** (JWT Authentication)
- **PostgreSQL** (Production) / **H2** (Testing)
- **JPA/Hibernate**
- **Maven**
- **Lombok**
- **Swagger** (API Documentation)

## Setup

### Prerequisites
- Java 17
- Maven 3.8+
- PostgreSQL 14+

### Installation
1. **Clone the repository**:
   ```bash
   git clone https://github.com/pearch001/pearchCash.git
   cd multicurrency-wallet-api
   ```

2. **Create a PostgreSQL database**:
   ```sql
   CREATE DATABASE wallet;
   ```

3. **Configure the database**:
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/wallet
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

4. **Build and run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The API will start at `http://localhost:8080`.

---

## API Documentation

### Authentication
| Endpoint       | Method | Description               | Example Request Body              |
|----------------|--------|---------------------------|------------------------------------|
| `/api/auth/register` | POST   | Register a new user        | `{"username":"user", "password":"pass", "email":"pass"}` |
| `/api/auth/login`    | POST   | Log in and get JWT token   | `{"username":"user", "password":"pass"}` |

### Account Management
| Endpoint                          | Method | Description                     | Example Request Body       |
|-----------------------------------|--------|---------------------------------|----------------------------|
| `/api/accounts`                   | POST   | Create a new account (currency) | `{"currency":"USD"}`       |
| `/api/accounts/{accountId}/deposit` | POST   | Deposit funds                  | `{"amount": 100.50}`       |
| `/api/accounts/{accountId}/withdraw` | POST   | Withdraw funds                | `{"amount": 50.25}`        |
| `/api/accounts/{accountId}/balance` | GET    | Get account balance           | -                          |

### Transactions
| Endpoint                    | Method | Description                  | Example Request Body                   |
|-----------------------------|--------|------------------------------|----------------------------------------|
| `/api/transfers`            | POST   | Transfer funds between users | `{"fromAccountId":1, "toAccountId":2, "amount":30}` |
| `/api/transactions/history` | GET    | View transaction history     | -                                      |

---

## Testing

### Run Tests
```bash
mvn test
```

### Example Requests (cURL)
**Register a user**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"username":"john", "password":"secret"}' http://localhost:8080/api/auth/register
```

**Get JWT token**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"username":"john", "password":"secret"}' http://localhost:8080/api/auth/login
```

**Deposit funds** (replace `<TOKEN>`):
```bash
curl -X POST -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"amount": 500}' http://localhost:8080/api/accounts/1/deposit
```

---

## Database Schema
![ER Diagram](https://via.placeholder.com/400x200?text=Wallet+ER+Diagram)
- **User**: `id`, `username`, `password`
- **Account**: `id`, `currency`, `balance`, `user_id`
- **Transaction**: `id`, `type`, `amount`, `currency`, `timestamp`, `from_account_id`, `to_account_id`

---

## Security
- 🔑 Passwords encrypted with **BCrypt**
- 🔒 JWT token expiration: 10 hours
- 🛡️ Role-based access control (users can only access their own accounts)

## Contributing
1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push to branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## License
MIT License (see [LICENSE](LICENSE))
```

---

### Additional Notes:
1. **Swagger UI**: Access API documentation at `http://localhost:8080/swagger-ui.html` after running the app.
2. **Performance**: Use `@Transactional` and database indexing for critical operations.
3. **Environment**: Add `application-dev.properties` for environment-specific configurations.

Let me know if you need help with Docker setup or deployment! 🚀
