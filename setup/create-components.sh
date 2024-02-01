#create configmaps
kubectl create configmap opa-server-policies --from-file=opa-server-files/policies
kubectl create configmap opa-server-data --from-file=opa-server-files/data

#create postgres service
kubectl apply -f kubernetes/postgres-external-service.yaml
kubectl apply -f kubernetes/postgres-external-endpoint.yaml

#create deployments
kubectl apply -f kubernetes/user-register-deployment.yaml
kubectl apply -f kubernetes/opa-server-deployment.yaml

#create services
kubectl apply -f kubernetes/opa-server-service.yaml
kubectl apply -f kubernetes/user-register-service.yaml

#create ingress
kubectl apply -f kubernetes/user-register-ingress.yaml

#prepare kubernetes cluster to use ingress
kubectl apply --filename https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/kind/deploy.yaml
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s