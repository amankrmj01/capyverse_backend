# Monitoring with Prometheus and Grafana

## 1. Prometheus Setup

1. Install Prometheus from https://prometheus.io/download/
2. Use the following sample `prometheus.yml` configuration:

```yaml
scrape_configs:
  - job_name: 'micronaut-app'
    metrics_path: /prometheus
    static_configs:
      - targets: ['host.docker.internal:8080'] # Use 'localhost:8080' if not running in Docker
```

3. Start Prometheus:
   ```
   prometheus --config.file=prometheus.yml
   ```

## 2. Grafana Setup

1. Install Grafana from https://grafana.com/grafana/download
2. Start Grafana:
   ```
   grafana-server
   ```
3. Open Grafana at http://localhost:3000
4. Add Prometheus as a data source (URL: `http://localhost:9090`)
5. Import or create dashboards to visualize your Micronaut metrics.

---

**Note:**
- If running your Micronaut app in Docker, use `host.docker.internal:8080` in Prometheus config.
- If running everything locally, use `localhost:8080`.
- The `/prometheus` endpoint serves metrics in plain text for Prometheus to scrape.
