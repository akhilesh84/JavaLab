#!/bin/bash

# Create namespace
kubectl apply -f k8slab-ns.yaml

# Deploy Kafka and kafka UI (Optional, not needed for WebAPI with OpenTelemetry)
kubectl apply -f kafka-deployment.yaml
kubectl apply -f kafka-ui.yaml

# Deploy WebAPI with OpenTelemetry
kubectl apply -f webapi-configmap.yaml
kubectl apply -f webapi-deployment.yaml

# Deploy Prometheus
kubectl apply -f prometheus-deployment.yaml

# Deploy Grafana
kubectl apply -f grafana-deployment.yaml

# Deploy Jaeger
kubectl apply -f jaeger-deployment.yaml

# Wait for deployments to be ready
kubectl wait --for=condition=available --timeout=300s deployment/kafka -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/kafka-ui -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/webapi -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/prometheus -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/grafana -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/jaeger -n k8slab-ns

echo "All deployments are ready!"
echo ""
echo "Access services:"
echo "  Kafka UI: kubectl port-forward -n k8slab-ns service/kafka-ui-service 8080:8080"
echo "  WebAPI: kubectl port-forward -n k8slab-ns service/webapi-service 8080:8080"
echo "  Prometheus: kubectl port-forward -n k8slab-ns service/prometheus-service 9090:9090"
echo "  OTEL Collector (OPTIONAL): kubectl port-forward -n k8slab-ns deployment/webapi 8889:8889"
echo "  Grafana: kubectl port-forward -n k8slab-ns service/grafana-service 3000:3000"
echo "  Jaeger UI: kubectl port-forward -n k8slab-ns service/jaeger-service 16686:16686"

echo "  Kakfa: kubectl port-forward -n k8slab-ns ervice/kafka-external 9093:9093"
echo "  Kafka UI: kubectl port-forward -n k8slab-ns service/kafka-ui 8085:8085"

#kubectl port-forward -n k8slab-ns service/webapi-service 8080:8080
#kubectl port-forward -n k8slab-ns service/prometheus-service 9090:9090
#kubectl port-forward -n k8slab-ns service/grafana-service 3080:3080

#kubectl apply -f k8slab-ns.yaml
#kubectl apply -f kafka-deployment.yaml
#kubectl apply -f kafka-ui.yaml
#
## Forward the port from K8s cluster to local machine
#kubectl port-forward svc/kafka-ui 8080:8080 -n k8slab-ns
