# Wavefront Proxy Exporter

This prometheus exporter accepts metrics from wavefront-proxy and returns them as the prometheus format. Works as a replacement of Wevafront service.

```
[ wavefront-proxy (PKS) ] ==> [ wavefront-proxy-exporter (this) ] <== [ prometheus]
```

## Build the exporeter

```
./mvnw clean package -DskipTests=true
```

Deploy this exporter somewhere


## Configure PKS Tile

Put the URL where you deployed above

![image](https://user-images.githubusercontent.com/106908/50850608-afc23d80-13bd-11e9-98cf-4c771b769ca7.png)

Any token is ok as it is not respected.

## Configure Prometheeus

Add a scrape config

```
- job_name: wavefront-proxy
  scrape_interval: 30s
  scrape_timeout: 10s
  metrics_path: /actuator/prometheus
  scheme: https
  static_configs:
  - targets:
    - wavefront-proxy-exporter.example.com:443
```

## Grafana Dashboard

Import [Dashboards](grafana) into your grafana

Screen captures are following:

![image](https://user-images.githubusercontent.com/106908/50850629-bea8f000-13bd-11e9-86e6-e663c00f4f6c.png)

![image](https://user-images.githubusercontent.com/106908/50850688-e8faad80-13bd-11e9-9f4a-4810d4a44472.png)

![image](https://user-images.githubusercontent.com/106908/50850659-d7b1a100-13bd-11e9-81c7-602df04479c5.png)

![image](https://user-images.githubusercontent.com/106908/50850705-f6b03300-13bd-11e9-9edb-342ddfb4dee8.png)

![image](https://user-images.githubusercontent.com/106908/50850738-134c6b00-13be-11e9-818f-b47b51179c46.png)
