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
  concurrently "pnpm run dev:backend" "pnpm run dev:frontend"
  ;;
test:backend)
  cd backend && ./gradlew test
  ;;
test:frontend)
  cd frontend && pnpm test
  ;;
test)
  concurrently "pnpm run test:backend" "pnpm run test:frontend"
  ;;
*)
  echo "Usage: $0 {start|exit}"
  exit 1
  ;;
esac
