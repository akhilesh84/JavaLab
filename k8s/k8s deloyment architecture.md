# Kubernetes Deployment Architecture

## Key Architecture Highlights

1. **Sidecar Pattern**: OTel Collector runs alongside WebAPI in the same pod, sharing the network namespace
2. **Service Mesh**: Each component is fronted by a Kubernetes Service for service discovery
3. **Observability Stack**: Complete telemetry pipeline from app → OTel → Prometheus → Grafana
4. **Message Queue**: Kafka for event streaming
5. **High Availability**: WebAPI runs with 2 replicas for redundancy
6. **Configuration Management**: ConfigMaps for OTel and Prometheus configurations

## Component Details

### WebAPI (Spring Boot Application)
- **Framework**: Spring Boot with Gradle
- **Language**: Java
- **Replicas**: 2
- **Ports**: 8080 (HTTP)
- **Features**:
    - REST API endpoints
    - Kafka message producer
    - OpenTelemetry instrumentation
    - Health checks (liveness & readiness)

### OpenTelemetry Collector (Sidecar)
- **Deployment**: Runs in the same pod as WebAPI
- **Receivers**: OTLP (gRPC on 4317, HTTP on 4318)
- **Processors**: Batch processing, memory limiting
- **Exporters**: Logging (debug), Prometheus (metrics on 8889)

### Kafka
- **Port**: 9092
- **Purpose**: Message broker for event streaming
- **UI**: Kafka UI for message inspection

### Prometheus
- **Port**: 9090
- **Purpose**: Metrics storage and querying
- **Scrape Target**: webapi-service:8889
- **Scrape Interval**: 15 seconds

### Grafana
- **Port**: 3000
- **Purpose**: Metrics visualization and dashboards
- **Data Source**: Prometheus
- **Credentials**: admin/admin (default)

## Access URLs

When using port-forwarding:

- **Kafka UI**: `http://localhost:8080`
- **WebAPI**: `http://localhost:8080` (or 8081 if Kafka UI is already on 8080)
- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3000`

## Telemetry Flow

1. Spring Boot application generates telemetry (traces, metrics, logs)
2. Telemetry sent to local OTel Collector via OTLP protocol (localhost:4318)
3. OTel Collector processes and exports to:
    - Console logs for debugging
    - Prometheus endpoint (:8889) for metrics scraping
4. Prometheus scrapes metrics from OTel Collector every 15 seconds
5. Grafana queries Prometheus to visualize metrics in dashboards


## Data Flow

1. External Request → WebAPI Service → WebAPI Pod
2. WebAPI generates telemetry → localhost:4318 → OTel Collector Sidecar
3. OTel Collector processes → Exports to:
     - Console logs (logging exporter)
     - Prometheus metrics endpoint :8889
4. Prometheus scrapes webapi-service:8889 every 15s
5. Grafana queries Prometheus for visualization
6. WebAPI sends messages → kafka-service:9092
7. Kafka UI reads from kafka-service:9092
