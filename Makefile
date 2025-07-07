DOCKER_DIR=docker
COMPOSE_LOCAL=$(DOCKER_DIR)/docker-compose.local.yml
COMPOSE_PROD=$(DOCKER_DIR)/docker-compose.prod.yml
COMPOSE_MONITORING=$(DOCKER_DIR)/monitoring/docker-compose.monitoring.yml
COMPOSE_K6=$(DOCKER_DIR)/k6/docker-compose.k6.yml

.PHONY: help up-local stop-local up-prod stop-prod up-monitoring stop-monitoring up-k6 stop-k6 logs clean build-backend build-frontend

help:
	@echo "=== Udong 프로젝트 Makefile 명령어 ==="
	@echo "make up-local           # 로컬 개발환경 docker-compose 올리기"
	@echo "make stop-local         # 로컬 개발환경 docker-compose 컨테이너 정지"
	@echo "make up-prod            # 운영/테스트 환경 docker-compose 올리기"
	@echo "make stop-prod          # 운영/테스트 환경 docker-compose 컨테이너 정지"
	@echo "make up-monitoring      # 모니터링 도구(Grafana/Prometheus) 올리기"
	@echo "make stop-monitoring    # 모니터링 도구 컨테이너 정지"
	@echo "make up-k6              # k6 부하테스트 컨테이너 실행"
	@echo "make stop-k6            # k6 부하테스트 컨테이너 정지"
	@echo "make logs               # 전체 docker 로그 보기"
	@echo "make clean              # 전체 컨테이너/볼륨 정리"
	@echo "make build-backend      # backend gradle 빌드"
	@echo "make build-frontend     # frontend 빌드"
	@echo "make help               # 도움말 보기"

# === 로컬 개발 환경 ===
up-local:
	docker compose -p local -f $(COMPOSE_LOCAL) up -d

stop-local:
	docker compose -p local -f $(COMPOSE_LOCAL) stop

# === 운영/테스트 환경 ===
up-prod:
	docker compose -p prod -f $(COMPOSE_PROD) up -d

stop-prod:
	docker compose -p prod -f $(COMPOSE_PROD) stop

# === 모니터링 (Grafana/Prometheus 등) ===
up-monitoring:
	docker compose -p monitoring -f $(COMPOSE_MONITORING) up -d

stop-monitoring:
	docker compose -p monitoring -f $(COMPOSE_MONITORING) stop

# === 부하테스트 (k6) ===
up-k6:
	docker compose -p k6 -f $(COMPOSE_K6) up -d

stop-k6:
	docker compose -p k6 -f $(COMPOSE_K6) stop

# === 로그, 빌드, 기타 ===
logs:
	docker compose -p local -f $(COMPOSE_LOCAL) logs -f

clean:
	docker system prune -f
	docker volume prune -f

build-backend:
	cd backend && ./gradlew clean build

build-frontend:
	cd frontend && npm install && npm run build
