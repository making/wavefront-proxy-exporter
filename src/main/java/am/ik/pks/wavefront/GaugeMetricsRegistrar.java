package am.ik.pks.wavefront;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.function.Tuple2;

import org.springframework.stereotype.Component;

@Component
public class GaugeMetricsRegistrar {

    private static final Logger log = LoggerFactory
        .getLogger(GaugeMetricsRegistrar.class);

    private final Cache<Tuple2<String, List<Tag>>, GaugeMetrics> cache;

    private final MeterRegistry meterRegistry;

    public GaugeMetricsRegistrar(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Cache<Tuple2<String, List<Tag>>, GaugeMetrics> caffeine = Caffeine.newBuilder() //
            .expireAfterWrite(2, TimeUnit.MINUTES) //
            .build();
        this.cache = CaffeineCacheMetrics.monitor(meterRegistry, caffeine, "gaugeMetrics");
    }

    public void register(GaugeMetrics metrics) {
        try {
            Tuple2<String, List<Tag>> key = metrics.key();
            cache.put(key, metrics);
            Gauge.builder(metrics.name(), cache, value -> {
                GaugeMetrics gaugeMetrics = value.get(key, x -> null);
                return gaugeMetrics == null ? Double.NaN : gaugeMetrics.value();
            }) //
                .tags(metrics.tags()) //
                .register(this.meterRegistry);
        } catch (IllegalArgumentException e) {
            log.debug("{}", e.getMessage());
        }
    }
}
