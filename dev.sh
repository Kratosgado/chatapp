#!/bin/bash

ACTION=${1:-run}
COMPONENT=${2:-all}

case "$ACTION" in
start)
  # Example: Start docker containers, set environment variables, etc.
  docker start postgis
  ;;
exit)
  # Example: Stop docker containers, cleanup, etc.
  docker stop postgis
  pkill -f "gradlew"
  pkill -f "pnpm dev"
  ;;
run)
  [ "$COMPONENT" != "api" ] && cd frontend && pnpm dev &
  [ "$COMPONENT" != "ui" ] && {
    cd backend
    # ./gradlew classes --continuous --quiet >/dev/null 2>&1 &
    ./gradlew classes --continuous &
    ./gradlew bootRun
  }
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
  echo "Usage: $0 {start|exit|run|build|test}"
  exit 1
  ;;
esac
