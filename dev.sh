#!/bin/bash

ACTION=${1:-dev}
COMPONENT=${2:-all}

case "$ACTION" in
start)
  # Example: Start docker containers, set environment variables, etc.
  docker start postgis
  ;;
exit)
  # Example: Stop docker containers, cleanup, etc.
  docker stop postgis
  ;;
dev)
  [ "$COMPONENT" != "api" ] && cd frontend && pnpm dev
  [ "$COMPONENT" != "ui" ] && cd backend && ./gradlew bootRun
  ;;
build)
  [ "$COMPONENT" != "api" ] && cd frontend && pnpm build
  [ "$COMPONENT" != "ui" ] && cd backend && ./gradlew build
  ;;
test)
  [ "$COMPONENT" != "api" ] && cd frontend && pnpm test
  [ "$COMPONENT" != "ui" ] && cd backend && ./gradlew test
  ;;
*)
  echo "Usage: $0 {start|exit|dev|build|test}"
  exit 1
  ;;
esac
