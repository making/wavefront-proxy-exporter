package am.ik.pks.wavefront;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import io.micrometer.core.instrument.Tag;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class GaugeMetrics {
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

	Tuple2<String, List<Tag>> key() {
		return Tuples.of(this.name, this.tags);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GaugeMetrics that = (GaugeMetrics) o;
		return Objects.equals(name, that.name) && Objects.equals(tags, that.tags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, tags);
	}
}
