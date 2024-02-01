docker run --name postgres-db -p 5432:5432 -e POSTGRES_PASSWORD=root -e POSTGRES_USER=postgres --rm -d postgres:14-alpine

#init kafka cluster
kind create cluster --name challenge-cluster --config kubernetes/kind-config.yaml
