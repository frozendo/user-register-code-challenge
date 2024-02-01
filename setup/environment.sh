docker run --name postgres-db -p 5432:5432 -e POSTGRES_PASSWORD=root -e POSTGRES_USER=postgres --rm -d postgres:14-alpine

#init kafka cluster
kind create cluster --name challenge-cluster --config kubernetes/kind-config.yaml

#prepare kafka cluster to use ingress
#kubectl apply --filename https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/kind/deploy.yaml

#kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s