# Docker & PostgreSQL Database Setup - COMPLETE

## ✅ Status: SUCCESSFULLY CONFIGURED

Your PostgreSQL database with Docker integration is now fully set up and ready to use!

---

## 📋 What Was Created

### 1. **Database Migrations (8 files)**
   - ✅ Master changelog coordination file
   - ✅ Users table (with XP/level system)
   - ✅ Activity types (with 5 seed categories)
   - ✅ Activities (with foreign keys)
   - ✅ Activity addresses (GPS coordinates)
   - ✅ Activity participants (enrollment tracking)
   - ✅ Achievements (definitions)
   - ✅ User achievements (user achievement records)
   - ✅ Preferences (user settings)

### 2. **Docker Configuration**
   - ✅ Enhanced docker-compose.yml with:
     - PostgreSQL 16 container
     - LocalStack S3 emulation
     - Health checks
     - Auto-restart policies
     - Performance tuning
     - Persistent volumes

### 3. **Helper Scripts**
   - ✅ db-helper.bat (Windows)
   - ✅ db-helper.sh (Linux/Mac)
   - ✅ init-s3.bat (Windows)
   - ✅ init-s3.sh (Linux/Mac)

### 4. **Documentation**
   - ✅ DATABASE_SETUP.md (comprehensive guide)
   - ✅ DOCKER_SETUP_SUMMARY.txt (quick reference)

---

## 🚀 Quick Start (Windows)

```powershell
# 1. Start Docker containers
docker-compose up -d

# 2. Wait for containers to be ready
Start-Sleep -Seconds 10

# 3. Verify containers are running
docker-compose ps

# 4. Start Spring Boot application
gradlew.bat bootRun

# 5. Access the application
# - API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Database: localhost:5432 (bootcamp_db)
# - S3 Bucket: http://localhost:4566
```

## 🚀 Quick Start (Linux/Mac)

```bash
# 1. Start Docker containers
docker-compose up -d

# 2. Wait for containers to be ready
sleep 10

# 3. Verify containers are running
docker-compose ps

# 4. Start Spring Boot application
./gradlew bootRun

# 5. Access the application
# - API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Database: localhost:5432 (bootcamp_db)
# - S3 Bucket: http://localhost:4566
```

---

## 📊 Database Configuration

| Property | Value |
|----------|-------|
| Database System | PostgreSQL 16 |
| Container Name | bootcamp_postgres |
| Host | localhost |
| Port | 5432 |
| Database | bootcamp_db |
| Username | postgres |
| Password | postgres |

---

## 📦 LocalStack S3 Configuration

| Property | Value |
|----------|-------|
| Service | LocalStack (AWS emulation) |
| Container Name | bootcamp_localstack |
| Endpoint | http://localhost:4566 |
| Region | us-east-1 |
| Bucket Name | bootcamp-bucket |
| Access Key | test |
| Secret Key | test |

---

## 📁 Database Schema (8 Tables)

1. **users** - User accounts with XP/Level system
2. **activity_types** - 5 predefined activity categories
3. **activities** - Activities created by users
4. **activity_addresses** - GPS coordinates for activities
5. **activity_participants** - Enrollment tracking
6. **achievements** - Achievement definitions
7. **user_achievements** - User achievement records
8. **preferences** - User settings (theme, notifications)

---

## 🛠️ Helper Script Commands

### Windows (use with: `.\db-helper.bat`)
```
db-helper.bat start      # Start all containers
db-helper.bat stop       # Stop all containers
db-helper.bat restart    # Restart all containers
db-helper.bat status     # Check container status
db-helper.bat logs       # View PostgreSQL logs
db-helper.bat psql       # Connect to database with psql
db-helper.bat clean      # Stop & remove containers + data
```

### Linux/Mac (use with: `./db-helper.sh`)
```
./db-helper.sh start     # Start all containers
./db-helper.sh stop      # Stop all containers
./db-helper.sh restart   # Restart all containers
./db-helper.sh status    # Check container status
./db-helper.sh logs      # View PostgreSQL logs
./db-helper.sh psql      # Connect to database with psql
./db-helper.sh clean     # Stop & remove containers + data
```

---

## 🔑 Key Features

✅ **Automatic Database Initialization**
- Liquibase automatically creates all tables when Spring Boot starts
- Initial data (Activity Types) automatically populated
- Foreign key relationships maintained

✅ **Health Monitoring**
- PostgreSQL health checks every 10 seconds
- Automatic container restart on failure

✅ **Persistent Data**
- Named volumes ensure data survives container restarts
- Data stored in: postgres_data, localstack_data

✅ **Performance Optimized**
- PostgreSQL tuned with 256MB shared buffers
- Max 100 connections configured
- Optimized for development use

---

## 🔍 Database Access Methods

### 1. PostgreSQL CLI
```bash
psql -h localhost -U postgres -d bootcamp_db
# Enter password: postgres
```

### 2. GUI Tools (pgAdmin, DBeaver)
- Host: localhost
- Port: 5432
- Database: bootcamp_db
- User: postgres
- Password: postgres

### 3. Spring Boot Application
- Automatically configured in application.yaml
- Migrations run automatically on startup

### 4. Docker Exec
```bash
docker exec -it bootcamp_postgres psql -U postgres -d bootcamp_db
```

---

## 📝 File Structure

```
Back-end/
├── docker-compose.yml              # Docker configuration
├── db-helper.bat                   # Windows helper script
├── db-helper.sh                    # Linux/Mac helper script
├── init-s3.bat                     # Windows S3 setup
├── init-s3.sh                      # Linux/Mac S3 setup
├── DATABASE_SETUP.md               # Detailed setup guide
├── DOCKER_SETUP_SUMMARY.txt        # Quick reference
├── build.gradle.kts                # Gradle config (Liquibase, PostgreSQL)
├── application.yaml                # Spring Boot config
├── src/main/resources/
│   ├── application.yaml
│   └── db/changelog/
│       ├── db.changelog-master.xml
│       └── migrations/
│           ├── 001-create-users.xml
│           ├── 002-create-activity-types.xml
│           ├── 003-create-activities.xml
│           ├── 004-create-activity-addresses.xml
│           ├── 005-create-activity-participants.xml
│           ├── 006-create-achievements.xml
│           ├── 007-create-user-achievements.xml
│           └── 008-create-preferences.xml
```

---

## ⚠️ Troubleshooting

### Containers won't start
```bash
# Check Docker is running
docker ps

# View container logs
docker-compose logs postgres
docker-compose logs localstack

# Restart Docker daemon
# Windows: Restart Docker Desktop
# Linux: sudo systemctl restart docker
```

### Database connection failed
```bash
# Wait longer for containers to be healthy
docker-compose ps

# Check container logs
docker-compose logs postgres

# Verify port 5432 is not in use
netstat -ano | findstr :5432  # Windows
lsof -i :5432                 # Linux/Mac
```

### Port 5432 already in use
- Modify `docker-compose.yml`: change `"5432:5432"` to `"5433:5432"`
- Update `application.yaml`: change `jdbc:postgresql://localhost:5432/` to `jdbc:postgresql://localhost:5433/`

### Migrations not running
- Verify `liquibase.enabled: true` in application.yaml
- Check Spring Boot logs for Liquibase errors
- Ensure all migration files are valid XML

### S3 bucket not found
- Run: `.\init-s3.bat` (Windows) or `./init-s3.sh` (Linux/Mac)
- Or manually: `docker exec bootcamp_localstack awslocal s3 mb s3://bootcamp-bucket`

---

## 📚 Next Steps

1. ✅ Read DATABASE_SETUP.md for detailed information
2. ✅ Start containers: `docker-compose up -d`
3. ✅ Run application: `./gradlew bootRun`
4. ✅ Access Swagger UI: http://localhost:8080/swagger-ui.html
5. ✅ Initialize S3: `.\init-s3.bat` or `./init-s3.sh`

---

## 📖 Documentation Links

- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Liquibase Docs](https://docs.liquibase.com/)
- [Docker Docs](https://docs.docker.com/)
- [Spring Boot Data JPA](https://spring.io/projects/spring-data-jpa)
- [LocalStack Docs](https://docs.localstack.cloud/)

---

## 🎉 You're All Set!

Your PostgreSQL database with Docker integration is ready to use. Start the containers and enjoy your fully containerized development environment!

**Questions?** Check DATABASE_SETUP.md for more detailed information and troubleshooting.

