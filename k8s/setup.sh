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

# Deploy Loki
kubectl apply -f loki-deployment.yaml

# Deploy Grafana
kubectl apply -f grafana-deployment.yaml

# Deploy Jaeger
kubectl apply -f tempo-deployment.yaml

# Wait for deployments to be ready
kubectl wait --for=condition=available --timeout=300s deployment/kafka -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/kafka-ui -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/webapi -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/prometheus -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/loki -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/grafana -n k8slab-ns
kubectl wait --for=condition=available --timeout=300s deployment/tempo -n k8slab-ns

echo "All deployments are ready!"
echo ""
echo "Access services:"
echo "  Kakfa: kubectl port-forward -n k8slab-ns service/kafka-external 9093:9093"
echo "  Kafka UI: kubectl port-forward -n k8slab-ns service/kafka-ui-service 8095:8095"

echo "  WebAPI: kubectl port-forward -n k8slab-ns service/webapi-service 8080:8080"
echo "  OTEL Collector (OPTIONAL): kubectl port-forward -n k8slab-ns deployment/webapi 8889:8889"

echo "  Prometheus: kubectl port-forward -n k8slab-ns service/prometheus-service 9090:9090"

echo "  Loki: kubectl port-forward -n k8slab-ns service/loki-service 3100:3100"

echo "  Grafana: kubectl port-forward -n k8slab-ns service/grafana-service 3000:3000"

# In principle te below port-forwards is all that we need. Forwward the webapi port to be able to access the same from
# host machine. And forward the grafana port to be able to access grafana dashboard from host machine.

#kubectl port-forward -n k8slab-ns service/webapi-service 8080:8080
#kubectl port-forward -n k8slab-ns service/grafana-service 3080:3080

#Once the forts are forwarded, we can setup data sources in grafana as below:
#  Prometheus Data Source for metrics:
#    Name: Prometheus
#    Type: Prometheus
#    URL: http://prometheus-service:9090
#  Loki Data Source for logs:
#    Name: Loki
#    Type: Loki
#    URL: http://loki-service:3100
#  Tempo Data Source for traces:
#    Name: Tempo
#    Type: Tempo


# To delete everything
# k delete all --all -n k8slab-ns