#!/bin/bash

# Database Helper Script for Linux/Mac

function show_help() {
    echo "Usage: ./db-helper.sh [command]"
    echo ""
    echo "Commands:"
    echo "  start       - Start PostgreSQL and LocalStack containers"
    echo "  stop        - Stop all containers"
    echo "  logs        - Show PostgreSQL logs"
    echo "  status      - Check container status"
    echo "  clean       - Stop and remove all containers and volumes"
    echo "  restart     - Restart all containers"
    echo "  psql        - Connect to database with psql"
}

if [ -z "$1" ]; then
    show_help
    exit 0
fi

case "$1" in
    start)
        echo "Starting Docker containers..."
        docker-compose up -d
        echo ""
        echo "Waiting for containers to be ready..."
        sleep 5
        docker-compose ps
        ;;
    stop)
        echo "Stopping Docker containers..."
        docker-compose down
        ;;
    logs)
        echo "PostgreSQL logs:"
        docker-compose logs postgres
        ;;
    status)
        echo "Container Status:"
        docker-compose ps
        echo ""
        echo "Container Health:"
        docker inspect bootcamp_postgres --format="{{.State.Health.Status}}"
        ;;
    clean)
        echo "WARNING: This will remove all containers and volumes including data!"
        read -p "Are you sure? (yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            docker-compose down -v
            echo "Cleanup complete."
        else
            echo "Cleanup cancelled."
        fi
        ;;
    restart)
        echo "Restarting Docker containers..."
        docker-compose down
        sleep 3
        docker-compose up -d
        echo ""
        docker-compose ps
        ;;
    psql)
        echo "Connecting to PostgreSQL database..."
        docker exec -it bootcamp_postgres psql -U postgres -d bootcamp_db
        ;;
    *)
        echo "Unknown command: $1"
        show_help
        exit 1
        ;;
esac

