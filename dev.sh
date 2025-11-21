#!/bin/bash

case "$1" in
start)
  # Example: Start docker containers, set environment variables, etc.
  docker start postgis
  ;;
exit)
  # Example: Stop docker containers, cleanup, etc.
  docker stop postgis
  ;;
dev:backend)
  cd backend && ./gradlew bootRun
  ;;
build:backend)
  cd backend && ./gradlew build
  ;;
dev:frontend)
  cd frontend && pnpm dev
  ;;
build:frontend)
  cd frontend && pnpm build
  ;;
dev)
  cd backend && ./gradlew bootRun &
  cd frontend && pnpm dev
  ;;
test:backend)
  cd backend && ./gradlew test
  ;;
test:frontend)
  cd frontend && pnpm test
  ;;
test)
  cd backend && ./gradlew test &
  cd frontend && pnpm test
  ;;
*)
  echo "Usage: $0 {start|exit}"
  exit 1
  ;;
esac
