package am.ik.pks.wavefront;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsConverter {

	private static final Logger log = LoggerFactory.getLogger(MetricsConverter.class);

	public GaugeMetrics convert(String line) {
		String[] l = line.split("\\s", 4);
		String name = l[0].replaceAll("\"", "");
		double value = Double.parseDouble(l[1]);
		long timestamp = Long.parseLong(l[2]);
		List<Tag> tags = new ArrayList<>();
		try (Scanner scanner = new Scanner(l[3].trim()).useDelimiter("\" \"")) {
			while (scanner.hasNext()) {
				String s = scanner.next().replaceAll("\"", "");
				String[] vals = s.split("=");
				Tag tag = Tag.of(vals[0], vals[1]);
				tags.add(tag);
			}
			return new GaugeMetrics(name, value, timestamp, tags);
		}
		catch (RuntimeException e) {
			log.error("error => " + line, e);
			throw e;
		}
	}
}
