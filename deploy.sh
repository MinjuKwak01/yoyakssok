#!/bin/bash
echo -e "배포 스크립트 시작\n\n"

# 환경 변수 확인
echo "Environment: ${ENVIRONMENT:-default}"
echo "Docker Image: ${DOCKER_IMAGE:-default}"

# 환경별 Compose 파일 결정
if [ "$ENVIRONMENT" = "prod" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
    echo "Using Production environment"
elif [ "$ENVIRONMENT" = "dev" ]; then
    COMPOSE_FILE="docker-compose.dev.yml"
    echo "Using Development environment"
else
    COMPOSE_FILE="docker-compose.yml"
    echo "Using Default environment"
fi

echo "Compose file: $COMPOSE_FILE"

# Compose 파일 존재 확인
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "Compose file $COMPOSE_FILE not found, using docker-compose.yml"
    COMPOSE_FILE="docker-compose.yml"
fi

# WAS 이미지 pull (backend 서비스로 수정)
echo "Backend 이미지 pull"
if [ -n "$DOCKER_IMAGE" ]; then
    docker pull $DOCKER_IMAGE
else
    docker compose -f $COMPOSE_FILE pull backend
fi

# 도커 컴포즈 실행
echo "Docker Compose 실행 중..."
if DOCKER_IMAGE=$DOCKER_IMAGE docker compose -f $COMPOSE_FILE up -d; then
    echo -e "Docker Compose 실행 완료\n\n"

    # 실행 상태 확인
    echo "컨테이너 상태 확인:"
    docker compose -f $COMPOSE_FILE ps
else
    echo -e "Docker Compose 실행 실패\n\n"
    exit 1
fi

echo "배포 스크립트 완료"
