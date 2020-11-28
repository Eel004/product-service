### Step to run application
1. profiles: development and production
2. run in local: we run it using develop profile with h2DB
``mvn spring-boot:run``
go to: http://localhost:8080 to check service available.
- Stop service command: ``kill $(lsof -t -i:8080)``
3. run in production profile:
docker-compose up
- setup docker in machine: https://docs.docker.com/get-started/
- make sure we have setup docker-compose: https://docs.docker.com/compose/install/
- pull mysql image from docker hub: https://hub.docker.com/_/mysql
- Navigate to product-service project folder and Build product service:
``docker build -t product-service .``
- start mysql container and setup DB
``docker run --name product-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=productDB -e MYSQL_USER=root -e MYSQL_PASSWORD=root -d mysql``
go to: http://localhost:8080 to check service available. 
- Stop service: ``docker-compose down``