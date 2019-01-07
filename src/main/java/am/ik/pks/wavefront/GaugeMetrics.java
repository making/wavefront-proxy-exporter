package am.ik.pks.wavefront;

import java.time.Instant;
import java.util.List;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GaugeMetrics {
	private static final Logger log = LoggerFactory.getLogger(GaugeMetrics.class);
	private final String name;
	private final double value;
	private final long timestamp;
	private final List<Tag> tags;

	public GaugeMetrics(String name, double value, long timestamp, List<Tag> tags) {
		this.name = name;
		this.value = value;
		this.timestamp = timestamp;
		this.tags = tags;
	}

	public void register(MeterRegistry meterRegistry) {
		try {
			Gauge.builder(this.name, this::value) //
					.tags(this.tags) //
					.register(meterRegistry);
		}
		catch (IllegalArgumentException e) {
			log.debug("{}", e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "GaugeMetrics{" + "name='" + name + '\'' + ", value=" + value
				+ ", timestamp=" + Instant.ofEpochSecond(this.timestamp) + ", tags="
				+ tags + '}';
	}

	String name() {
		return name;
	}

	double value() {
		return value;
	}

	long timestamp() {
		return timestamp;
	}

	List<Tag> tags() {
		return tags;
	}
}
