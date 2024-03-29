#create configmaps
kubectl create configmap opa-server-policies --from-file=opa-server-files/policies
kubectl create configmap opa-server-data --from-file=opa-server-files/data
kubectl create configmap opa-server-config --from-file=opa-server-files/config

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