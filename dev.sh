#!/bin/bash

ACTION=${1:-run}
COMPONENT=${2:-all}

# PIDs array to keep track of background processes
PIDS=()

# Cleanup function to kill background processes
cleanup() {
  echo ""
  echo "Stopping all processes..."
  for pid in "${PIDS[@]}"; do
    if ps -p $pid >/dev/null; then
      kill $pid 2>/dev/null
    fi
  done
  # Also try pkill for safety if valid PIDs were lost or spawned subprocesses
  if [ "$ACTION" == "run" ]; then
    pkill -P $$
  fi
  exit
}

# Trap SIGINT (Ctrl+C) and SIGTERM
trap cleanup SIGINT SIGTERM

case "$ACTION" in
start)
  docker start postgis
  ;;
exit)
  docker stop postgis
  pkill -f "gradlew"
  pkill -f "pnpm dev"
  ;;
run)
  if [ "$COMPONENT" != "api" ]; then
    echo "Starting Frontend..."
    # Run in subshell to not affect current directory
    (cd frontend && pnpm dev) &
    PIDS+=($!)
  fi

  if [ "$COMPONENT" != "ui" ]; then
    echo "Starting Backend (Continuous Compilation)..."
    # Run classes task in background to trigger recompilation on changes
    (cd backend && ./gradlew classes --continuous --quiet) &
    PIDS+=($!)

    echo "Starting Backend (BootRun)..."
    # Run bootRun in background so we can wait for everything
    (cd backend && SEED=true ./gradlew bootRun) &
    PIDS+=($!)
  fi

  # Wait for all background processes to finish (or until interrupted)
  wait
  ;;
build)
  if [ "$COMPONENT" != "api" ]; then
    echo "Building Frontend..."
    (cd frontend && pnpm build)
  fi
  if [ "$COMPONENT" != "ui" ]; then
    echo "Building Backend..."
    (cd backend && ./gradlew build)
  fi
  ;;
test)
  if [ "$COMPONENT" != "api" ]; then
    echo "Testing Frontend..."
    (cd frontend && pnpm test)
  fi
  if [ "$COMPONENT" != "ui" ]; then
    echo "Testing Backend..."
    (cd backend && ./gradlew test)
  fi
  ;;
*)
  echo "Usage: $0 {start|exit|run|build|test}"
  exit 1
  ;;
esac
