PROJECT_COMPOSE=docker-compose.yml
MONITORING_COMPOSE=docker/monitoring/docker-compose.monitoring.yml
K6_COMPOSE=docker/k6/docker-compose.k6.yml

start-project:
	docker compose -f $(PROJECT_COMPOSE) --env-file .env.prod up --build -d

stop-project:
	docker compose -f $(PROJECT_COMPOSE) stop

logs-project:
	docker compose -f $(PROJECT_COMPOSE) logs -f

start-monitoring:
	docker compose -f $(MONITORING_COMPOSE) up --build -d

stop-monitoring:
	docker compose -f $(MONITORING_COMPOSE) stop

logs-monitoring:
	docker compose -f $(MONITORING_COMPOSE) logs -f

start-k6:
	docker compose -f $(K6_COMPOSE) up --build -d

stop-k6:
	docker compose -f $(K6_COMPOSE) down

logs-k6:
	docker compose -f $(K6_COMPOSE) logs -f

restart-spring:
	docker compose -f $(PROJECT_COMPOSE) restart spring
