# Kubernetes Deployment Architecture Diagram
## Based on k8s YAML Definitions

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                  Namespace: k8slab-ns                                    │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                   External Access Layer                                  │
│                                                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │
│  │ LoadBalancer │  │ LoadBalancer │  │ LoadBalancer │  │ LoadBalancer │  │ NodePort │ │
│  │   WebAPI     │  │  Prometheus  │  │   Grafana    │  │   Jaeger     │  │  Kafka   │ │
│  │   :8080      │  │   :9090      │  │   :3000      │  │   :16686     │  │  :30093  │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └────┬─────┘ │
└─────────┼──────────────────┼──────────────────┼──────────────────┼──────────────┼───────┘
          │                  │                  │                  │              │
          │                  │                  │                  │              │
┌─────────▼──────────────────▼──────────────────▼──────────────────▼──────────────▼───────┐
│                              Kubernetes Services Layer                                   │
│                                                                                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │
│  │ webapi-service  │  │prometheus-service│  │ grafana-service │  │ jaeger-service  │   │
│  │ Type: LB        │  │ Type: LB         │  │ Type: LB        │  │ Type: LB        │   │
│  │ Port: 8080      │  │ Port: 9090       │  │ Port: 3000      │  │ UI: 16686       │   │
│  └────────┬────────┘  └────────┬─────────┘  └────────┬────────┘  │ gRPC: 4317      │   │
│           │                    │                      │           │ HTTP: 4318      │   │
│           │                    │                      │           └────────┬────────┘   │
│           │                    │                      │                    │            │
│  ┌────────┴────────┐  ┌────────┴─────────┐  ┌────────┴────────┐  ┌────────┴────────┐  │
│  │  Scrape: 8889   │  │  Query Data      │  │  Query Traces   │  │  Receive Traces │  │
│  └────────┬────────┘  └──────────────────┘  └─────────────────┘  └─────────────────┘  │
│           │                    ▲                      ▲                    ▲            │
│           │                    │                      │                    │            │
│  ┌────────┴────────┐  ┌────────┴─────────┐  ┌────────┴────────┐  ┌────────────────┐   │
│  │  kafka-service  │  │                  │  │                 │  │                │   │
│  │  (ClusterIP)    │  │                  │  │                 │  │                │   │
│  │  Port: 9092     │  │                  │  │                 │  │                │   │
│  └────────┬────────┘  │                  │  │                 │  │                │   │
└───────────┼───────────┴──────────────────┴──┴─────────────────┴──┴────────────────────┘
            │
            │
┌───────────▼───────────────────────────────────────────────────────────────────────────┐
│                                  Pods & Containers Layer                               │
│                                                                                        │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐  │
│  │                          WebAPI Pod (Replica: 1)                                │  │
│  │  ┌──────────────────────────────────┐  ┌─────────────────────────────────────┐ │  │
│  │  │   webapi Container               │  │  otel-collector Sidecar             │ │  │
│  │  │                                  │  │                                     │ │  │
│  │  │  Image: javaapi:latest           │  │  Image: otel-collector-contrib    │ │  │
│  │  │  Port: 8080                      │  │         :0.141.0                   │ │  │
│  │  │                                  │  │                                     │ │  │
│  │  │  Spring Boot App with:           │  │  Receivers:                        │ │  │
│  │  │  - REST APIs                     │  │  - OTLP gRPC :4317                 │ │  │
│  │  │  - H2 Database                   │  │  - OTLP HTTP :4318 ◄───┐           │ │  │
│  │  │  - Actuator :8080/actuator       │  │                        │           │ │  │
│  │  │  - H2 Console :8080/h2-console   │  │  Processors:           │           │ │  │
│  │  │  - OpenTelemetry Java Agent      │  │  - batch               │           │ │  │
│  │  │                                  │  │  - memory_limiter      │           │ │  │
│  │  │  Telemetry Export:               │  │                        │           │ │  │
│  │  │  - Traces ───────────────────────┼──┼──► localhost:4318     │           │ │  │
│  │  │  - Metrics (Micrometer) ─────────┼──┼────────────────────────┘           │ │  │
│  │  │  - Logs ─────────────────────────┼──┼──► localhost:4318                  │ │  │
│  │  │                                  │  │                                     │ │  │
│  │  │  Env Variables:                  │  │  Exporters:                        │ │  │
│  │  │  - OTEL_SERVICE_NAME=webapi      │  │  - debug (console logs)            │ │  │
│  │  │  - OTEL_EXPORTER_OTLP_ENDPOINT   │  │  - prometheus :8889 ───────────┐   │ │  │
│  │  │    =http://localhost:4318        │  │  - otlp/jaeger (gRPC)          │   │ │  │
│  │  │  - OTEL_TRACES_EXPORTER=otlp     │  │    endpoint: jaeger-service:4317   │ │  │
│  │  │  - OTEL_METRICS_EXPORTER=none    │  │                                │   │ │  │
│  │  │                                  │  │  ConfigMap:                    │   │ │  │
│  │  │  Health Probes:                  │  │  - otel-collector-config       │   │ │  │
│  │  │  - Startup: /actuator/health/    │  │                                │   │ │  │
│  │  │    liveness (120s timeout)       │  │                                │   │ │  │
│  │  │  - Liveness: /actuator/health/   │  │                                │   │ │  │
│  │  │    liveness                      │  │                                │   │ │  │
│  │  │  - Readiness: /actuator/health/  │  │                                │   │ │  │
│  │  │    readiness                     │  │                                │   │ │  │
│  │  │                                  │  │                                │   │ │  │
│  │  │  Resources:                      │  │  Resources:                    │   │ │  │
│  │  │  - Memory: 512Mi-1Gi             │  │  - Memory: 128Mi-256Mi         │   │ │  │
│  │  │  - CPU: 250m-500m                │  │  - CPU: 100m-200m              │   │ │  │
│  │  └──────────────────────────────────┘  └─────────────────────────────────┘ │  │
│  │                                                         │                    │  │
│  │  Shared Pod Network: localhost                         │                    │  │
│  └─────────────────────────────────────────────────────────┼────────────────────┘  │
│                                                            │                       │
│  ┌─────────────────────────────────────────────────────────┼────────────────────┐  │
│  │                      Prometheus Pod (Replica: 1)        │                    │  │
│  │  ┌──────────────────────────────────────────────────────▼─────────────────┐ │  │
│  │  │  prometheus Container                                                   │ │  │
│  │  │  Image: prom/prometheus:latest                                          │ │  │
│  │  │  Port: 9090                                                             │ │  │
│  │  │                                                                          │ │  │
│  │  │  Scrape Config:                                                         │ │  │
│  │  │  - Job: otel-collector                                                  │ │  │
│  │  │  - Target: webapi-service:8889                                          │ │  │
│  │  │  - Interval: 15s                                                        │ │  │
│  │  │                                                                          │ │  │
│  │  │  ConfigMap: prometheus-config                                           │ │  │
│  │  └──────────────────────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                     │
│  ┌──────────────────────────────────────────────────────────────────────────────┐  │
│  │                      Grafana Pod (Replica: 1)                                │  │
│  │  ┌────────────────────────────────────────────────────────────────────────┐ │  │
│  │  │  grafana Container                                                      │ │  │
│  │  │  Image: grafana/grafana:latest                                         │ │  │
│  │  │  Port: 3000                                                            │ │  │
│  │  │                                                                         │ │  │
│  │  │  Credentials:                                                          │ │  │
│  │  │  - Username: admin                                                     │ │  │
│  │  │  - Password: admin                                                     │ │  │
│  │  │                                                                         │ │  │
│  │  │  Datasources (to configure):                                           │ │  │
│  │  │  - Prometheus: http://prometheus-service:9090                          │ │  │
│  │  │  - Jaeger: http://jaeger-service:16686                                 │ │  │
│  │  └────────────────────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                     │
│  ┌──────────────────────────────────────────────────────────────────────────────┐  │
│  │                      Jaeger Pod (Replica: 1)                                 │  │
│  │  ┌────────────────────────────────────────────────────────────────────────┐ │  │
│  │  │  jaeger Container (All-in-One)                                         │ │  │
│  │  │  Image: jaegertracing/all-in-one:1.76.0                                │ │  │
│  │  │                                                                         │ │  │
│  │  │  Ports:                                                                 │ │  │
│  │  │  - UI: 16686                                                           │ │  │
│  │  │  - OTLP gRPC: 4317 ◄─── Receives traces from OTEL Collector           │ │  │
│  │  │  - OTLP HTTP: 4318                                                     │ │  │
│  │  │  - Model: 14250                                                        │ │  │
│  │  │                                                                         │ │  │
│  │  │  Features:                                                             │ │  │
│  │  │  - Trace collection                                                    │ │  │
│  │  │  - Trace storage (in-memory)                                           │ │  │
│  │  │  - Trace visualization UI                                              │ │  │
│  │  │  - Service dependency graph                                            │ │  │
│  │  │                                                                         │ │  │
│  │  │  Resources:                                                            │ │  │
│  │  │  - Memory: 256Mi-512Mi                                                 │ │  │
│  │  │  - CPU: 200m-500m                                                      │ │  │
│  │  └────────────────────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                     │
│  ┌──────────────────────────────────────────────────────────────────────────────┐  │
│  │                      Kafka Pod (Replica: 1)                                  │  │
│  │  ┌────────────────────────────────────────────────────────────────────────┐ │  │
│  │  │  kafka Container                                                        │ │  │
│  │  │  Image: apache/kafka:latest                                            │ │  │
│  │  │                                                                         │ │  │
│  │  │  Ports:                                                                 │ │  │
│  │  │  - Internal: 9092 (PLAINTEXT)                                          │ │  │
│  │  │  - External: 9093 (PLAINTEXT) → NodePort 30093                         │ │  │
│  │  │  - Controller: 9094                                                    │ │  │
│  │  │                                                                         │ │  │
│  │  │  Mode: KRaft (broker + controller)                                     │ │  │
│  │  │  Replication Factor: 1                                                 │ │  │
│  │  │                                                                         │ │  │
│  │  │  Services:                                                             │ │  │
│  │  │  - kafka (ClusterIP) :9092                                             │ │  │
│  │  │  - kafka-external (NodePort) :30093                                    │ │  │
│  │  └────────────────────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                     │
│  ┌──────────────────────────────────────────────────────────────────────────────┐  │
│  │                      Kafka UI Pod (Replica: 1)                               │  │
│  │  ┌────────────────────────────────────────────────────────────────────────┐ │  │
│  │  │  kafka-ui Container                                                     │ │  │
│  │  │  Image: provectuslabs/kafka-ui:latest                                  │ │  │
│  │  │  Port: 8080                                                            │ │  │
│  │  │                                                                         │ │  │
│  │  │  Connected to: kafka:9092                                              │ │  │
│  │  │  Cluster Name: local                                                   │ │  │
│  │  │                                                                         │ │  │
│  │  │  Service: kafka-ui (ClusterIP) :8080                                   │ │  │
│  │  └────────────────────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               ConfigMaps & Configuration                             │
│                                                                                      │
│  ┌────────────────────────────┐  ┌────────────────────────────┐                    │
│  │  otel-collector-config     │  │  prometheus-config         │                    │
│  │  (webapi-configmap.yaml)   │  │  (prometheus-deployment)   │                    │
│  │                            │  │                            │                    │
│  │  - OTLP Receivers          │  │  - Scrape interval: 15s    │                    │
│  │  - Batch processor         │  │  - Job: otel-collector     │                    │
│  │  - Memory limiter          │  │  - Target:                 │                    │
│  │  - Debug exporter          │  │    webapi-service:8889     │                    │
│  │  - Prometheus exporter     │  └────────────────────────────┘                    │
│  │  - Jaeger OTLP exporter    │                                                    │
│  └────────────────────────────┘                                                    │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                   Data Flow Diagram                                  │
│                                                                                      │
│  ┌──────────────┐                                                                   │
│  │   External   │                                                                   │
│  │   Request    │                                                                   │
│  └──────┬───────┘                                                                   │
│         │                                                                            │
│         ▼                                                                            │
│  ┌──────────────────────────────────────────────────────────────┐                   │
│  │  webapi-service:8080 (LoadBalancer)                          │                   │
│  └──────┬───────────────────────────────────────────────────────┘                   │
│         │                                                                            │
│         ▼                                                                            │
│  ┌──────────────────────────────────────────────────────────────┐                   │
│  │  WebAPI Container :8080                                       │                   │
│  │  - Processes HTTP request                                     │                   │
│  │  - OpenTelemetry Java Agent captures:                         │                   │
│  │    • HTTP request details (automatic)                         │                   │
│  │    • Database queries (automatic)                             │                   │
│  │    • Creates trace spans (automatic)                          │                   │
│  └──────┬───────────────────────────────────────────────────────┘                   │
│         │                                                                            │
│         │ Exports telemetry to localhost:4318                                       │
│         ▼                                                                            │
│  ┌──────────────────────────────────────────────────────────────┐                   │
│  │  OTEL Collector Sidecar :4318                                │                   │
│  │  - Receives: Traces, Metrics (future), Logs                  │                   │
│  │  - Processes: Batches, Memory limits                         │                   │
│  │  - Exports to:                                               │                   │
│  │    1. Jaeger (traces) → jaeger-service:4317                  │                   │
│  │    2. Prometheus endpoint :8889 (metrics)                    │                   │
│  │    3. Debug console (logs)                                   │                   │
│  └──────┬────────────────────────┬──────────────────────────────┘                   │
│         │                        │                                                   │
│         │ Traces (gRPC)          │ Exposes :8889                                     │
│         ▼                        ▼                                                   │
│  ┌──────────────────┐     ┌──────────────────┐                                      │
│  │  Jaeger :4317    │     │  Prometheus      │                                      │
│  │  - Stores traces │     │  - Scrapes :8889 │                                      │
│  │  - UI :16686     │     │  - Stores metrics│                                      │
│  └──────────────────┘     └─────────┬────────┘                                      │
│         ▲                           │                                                │
│         │                           │ Queries                                        │
│         │ Queries                   ▼                                                │
│         │                  ┌──────────────────┐                                      │
│         └──────────────────┤  Grafana :3000   │                                      │
│                            │  - Visualizations│                                      │
│                            │  - Dashboards    │                                      │
│                            └──────────────────┘                                      │
│                                                                                      │
│  Optional Kafka Integration (when enabled):                                         │
│                                                                                      │
│  WebAPI ───► kafka:9092 (publishes messages)                                        │
│               │                                                                      │
│               └───► Kafka UI :8080 (monitors topics, messages)                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              Key Architectural Patterns                              │
│                                                                                      │
│  1. **Sidecar Pattern**                                                             │
│     - OTEL Collector runs alongside WebAPI in same pod                              │
│     - Shares pod network namespace (localhost communication)                        │
│     - Independent lifecycle and resource management                                 │
│                                                                                      │
│  2. **Service Mesh (Observability)**                                                │
│     - Telemetry pipeline: App → Collector → Backends                               │
│     - Centralized telemetry processing and routing                                  │
│     - Metrics, Traces, and Logs separation                                          │
│                                                                                      │
│  3. **Zero-Code Instrumentation**                                                   │
│     - OpenTelemetry Java Agent for automatic tracing                                │
│     - No code changes needed for basic observability                                │
│     - Configured via environment variables                                          │
│                                                                                      │
│  4. **Configuration as Code**                                                       │
│     - All configuration in ConfigMaps                                               │
│     - Declarative YAML definitions                                                  │
│     - Version controlled and reproducible                                           │
│                                                                                      │
│  5. **Service Discovery**                                                           │
│     - Kubernetes DNS for inter-service communication                                │
│     - Service names resolve within namespace                                        │
│     - Example: jaeger-service, prometheus-service, kafka                            │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                 Resource Summary                                     │
│                                                                                      │
│  Namespace: k8slab-ns                                                               │
│                                                                                      │
│  Deployments: 5 (+ 1 optional Kafka)                                                │
│  ├── webapi         (1 replica, 2 containers)                                       │
│  ├── prometheus     (1 replica)                                                     │
│  ├── grafana        (1 replica)                                                     │
│  ├── jaeger         (1 replica)                                                     │
│  ├── kafka          (1 replica) - optional                                          │
│  └── kafka-ui       (1 replica) - optional                                          │
│                                                                                      │
│  Services: 7                                                                         │
│  ├── webapi-service      (LoadBalancer :8080)                                       │
│  ├── prometheus-service  (LoadBalancer :9090)                                       │
│  ├── grafana-service     (LoadBalancer :3000)                                       │
│  ├── jaeger-service      (LoadBalancer :16686, :4317, :4318)                        │
│  ├── kafka               (ClusterIP :9092) - optional                               │
│  ├── kafka-external      (NodePort :30093) - optional                               │
│  └── kafka-ui            (ClusterIP :8080) - optional                               │
│                                                                                      │
│  ConfigMaps: 2                                                                       │
│  ├── otel-collector-config                                                          │
│  └── prometheus-config                                                              │
│                                                                                      │
│  Total Resource Requests:                                                           │
│  - Memory: ~1.3 GB (webapi + observability stack)                                   │
│  - CPU: ~1.0 cores                                                                  │
│                                                                                      │
│  Total Resource Limits:                                                             │
│  - Memory: ~2.3 GB                                                                  │
│  - CPU: ~1.7 cores                                                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                  Access Information                                  │
│                                                                                      │
│  Port Forward Commands:                                                             │
│                                                                                      │
│  WebAPI:                                                                            │
│  kubectl port-forward -n k8slab-ns svc/webapi-service 8080:8080                     │
│  • API: http://localhost:8080/api/*                                                │
│  • Actuator: http://localhost:8080/actuator                                        │
│  • H2 Console: http://localhost:8080/h2-console                                    │
│                                                                                      │
│  Prometheus:                                                                         │
│  kubectl port-forward -n k8slab-ns svc/prometheus-service 9090:9090                 │
│  • URL: http://localhost:9090                                                       │
│  • Targets: http://localhost:9090/targets                                          │
│                                                                                      │
│  Grafana:                                                                           │
│  kubectl port-forward -n k8slab-ns svc/grafana-service 3000:3000                    │
│  • URL: http://localhost:3000                                                       │
│  • Login: admin / admin                                                            │
│                                                                                      │
│  Jaeger:                                                                            │
│  kubectl port-forward -n k8slab-ns svc/jaeger-service 16686:16686                   │
│  • URL: http://localhost:16686                                                      │
│  • Search traces for service: webapi                                               │
│                                                                                      │
│  Kafka UI (if enabled):                                                             │
│  kubectl port-forward -n k8slab-ns svc/kafka-ui 8080:8080                           │
│  • URL: http://localhost:8080                                                       │
│                                                                                      │
│  OTEL Collector Metrics (direct):                                                  │
│  kubectl port-forward -n k8slab-ns deployment/webapi 8889:8889                      │
│  • URL: http://localhost:8889/metrics                                               │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

## Summary

This architecture implements a **modern cloud-native observability stack** with:

- **Application**: Spring Boot REST API with H2 database
- **Instrumentation**: OpenTelemetry Java Agent (zero-code)
- **Collection**: OpenTelemetry Collector (sidecar pattern)
- **Metrics**: Prometheus + Grafana
- **Traces**: Jaeger (OTLP receiver)
- **Optional**: Kafka message broker with UI

All components are deployed in the `k8slab-ns` namespace with proper service discovery, health checks, and resource management.

