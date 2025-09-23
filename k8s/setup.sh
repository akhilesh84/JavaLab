kubectl apply -f k8slab-ns.yaml
kubectl apply -f kafka-deployment.yaml
kubectl apply -f kafka-ui.yaml
kubectl port-forward svc/kafka-ui 8080:8080 -n k8slab-ns
