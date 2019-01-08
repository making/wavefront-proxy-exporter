package am.ik.pks.wavefront;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.function.Tuple2;

import org.springframework.stereotype.Component;

@Component
public class GaugeMetricsRegistrar {
	private static final Logger log = LoggerFactory.getLogger(GaugeMetricsRegistrar.class);
	private final ConcurrentMap<Tuple2<String, List<Tag>>, GaugeMetrics> cache = new ConcurrentHashMap<>();
	private final MeterRegistry meterRegistry;

	public GaugeMetricsRegistrar(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	public void register(GaugeMetrics metrics) {
		try {
			Tuple2<String, List<Tag>> key = metrics.key();
			cache.put(key, metrics);
			Gauge.builder(metrics.name(), cache, value -> value.get(key).value()) //
					.tags(metrics.tags()) //
					.register(this.meterRegistry);
		}
		catch (IllegalArgumentException e) {
			log.debug("{}", e.getMessage());
		}
	}
}
