### My playground for practicing JAVA concepts and coding exercises. I often use this space to experiment with new ideas and cloud technologies. Feel free to explore the code and provide feedback!

#### Building docker image
Run the below at teh root of he repository to build the docker image:
```bash
docker build -t javaapi:latest .
```

Once the image is built, you can run the script file to setup the kubernetes deployment and service:
```bash
cd k8s/

./setup.sh
```

This will create a deployment and a service in your kubernetes cluster. Upon successful deployment, you should see output similar to:
```
Kafka UI: kubectl port-forward -n k8slab-ns service/kafka-ui-service 8080:8080"
WebAPI: kubectl port-forward -n k8slab-ns service/webapi-service 8080:8080"
Prometheus: kubectl port-forward -n k8slab-ns service/prometheus-service 9090:9090"
Loki: kubectl port-forward -n k8slab-ns service/loki-service 3100:3100"
OTEL Collector (OPTIONAL): kubectl port-forward -n k8slab-ns deployment/webapi 8889:8889"
Grafana: kubectl port-forward -n k8slab-ns service/grafana-service 3000:3000"
Jaeger UI: kubectl port-forward -n k8slab-ns service/jaeger-service 16686:16686"
Kakfa: kubectl port-forward -n k8slab-ns ervice/kafka-external 9093:9093"
Kafka UI: kubectl port-forward -n k8slab-ns service/kafka-ui 8085:8085"
```

To be able to access the application from outside the cluster, you can use port-forwarding as shown in the output above.