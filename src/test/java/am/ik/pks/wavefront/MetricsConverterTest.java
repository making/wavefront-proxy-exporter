package am.ik.pks.wavefront;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.micrometer.core.instrument.Tag;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricsConverterTest {
	@Test
	public void convert() {
		MetricsConverter converter = new MetricsConverter();
		GaugeMetrics gauge = converter.convert(
				"\"pks.heapster.sys.container.memory.major.page_faults.rate\" 0.0 1546844220 source=\"172.16.2.16\" "
						+ "\"nodename\"=\"fd5d1ff7-115b-428b-b496-171e2b4c05a5\" "
						+ "\"cluster\"=\"service-instance_16bd67d5-acf3-4e97-9f04-d7dfab9cc83a\" \"container_name\"=\"kubepods/burstable/pod0a22bfae-1102-11e9-8f9f-005056a675e6\" \"type\"=\"sys_container\"\n");

		assertThat(gauge).isNotNull();
		assertThat(gauge.name())
				.isEqualTo("pks.heapster.sys.container.memory.major.page_faults.rate");
		assertThat(gauge.value()).isEqualTo(0.0);
		assertThat(gauge.timestamp()).isEqualTo(1546844220L);
		assertThat(gauge.tags()).containsExactly(Tag.of("source", "172.16.2.16"),
				Tag.of("nodename", "fd5d1ff7-115b-428b-b496-171e2b4c05a5"),
				Tag.of("cluster",
						"service-instance_16bd67d5-acf3-4e97" + "-9f04-d7dfab9cc83a"),
				Tag.of("container_name",
						"kubepods/burstable/pod0a22bfae-1102-11e9-8f9f-005056a675e6"),
				Tag.of("type", "sys_container"));
	}

	@Test
	public void convertShortenDoubleFormat() {
		MetricsConverter converter = new MetricsConverter();
		GaugeMetrics gauge = converter.convert(
				"\"pks.heapster.sys_container.disk.io.write_bytes.rate\" 1.04376242807498E8 1546844220 source=\"172.16.2.14\" "
						+ "\"cluster\"=\"service-instance_16bd67d5-acf3-4e97-9f04-d7dfab9cc83a\" \"nodename\"=\"41804e59-c66f-4e5f-9216-6c59d087ce61\" \"resource_id\"=\"/dev/sdb\" "
						+ "\"container_name\"=\"kubepods/burstable\" \"type\"=\"sys_container\"");

		assertThat(gauge).isNotNull();
		assertThat(gauge.name())
				.isEqualTo("pks.heapster.sys_container.disk.io.write_bytes.rate");
		assertThat(gauge.value()).isEqualTo(1.04376242807498E8);
		assertThat(gauge.timestamp()).isEqualTo(1546844220L);
		assertThat(gauge.tags()).containsExactly(Tag.of("source", "172.16.2.14"),
				Tag.of("cluster",
						"service-instance_16bd67d5-acf3-4e97-9f04-d7dfab9cc83a"),
				Tag.of("nodename", "41804e59-c66f-4e5f" + "-9216-6c59d087ce61"),
				Tag.of("resource_id", "/dev/sdb"),
				Tag.of("container_name", "kubepods/burstable"),
				Tag.of("type", "sys_container"));
	}

	@Test
	public void tagWithSpace() {
		MetricsConverter converter = new MetricsConverter();
		GaugeMetrics gauge = converter.convert(
				"\"pks.kube.node.info.gauge\" 1.0 1546852320 source=\"wavefront-proxy-cd87869b8-gfh4l\" "
						+ "\"cluster\"=\"service-instance_16bd67d5-acf3-4e97-9f04-d7dfab9cc83a\" "
						+ "\"node\"=\"0cd64532-f732-4702-b878-18b843ce00a6\" \"kubeproxy_version\"=\"v1.11.6\" \"kernel_version\"=\"4.15.0-42-generic\" \"os_image\"=\"Ubuntu 16.04.5 LTS\" "
						+ "\"provider_id\"=\"vsphere://4226652c-5b80-ea02-b8dc-924b2a7a948e\" \"kubelet_version\"=\"v1.11.6\" \"container_runtime_version\"=\"docker://17.12.1-ce\" "
						+ "\"url\"=\"http://localhost:8080/metrics\"");

		assertThat(gauge).isNotNull();
		assertThat(gauge.name()).isEqualTo("pks.kube.node.info.gauge");
		assertThat(gauge.value()).isEqualTo(1.0);
		assertThat(gauge.timestamp()).isEqualTo(1546852320);
		assertThat(gauge.tags()).containsExactly(
				Tag.of("source", "wavefront-proxy-cd87869b8-gfh4l"),
				Tag.of("cluster",
						"service-instance_16bd67d5-acf3-4e97-9f04-d7dfab9cc83a"),
				Tag.of("node", "0cd64532-f732-4702-b878-18b843ce00a6"),
				Tag.of("kubeproxy_version", "v1.11.6"),
				Tag.of("kernel_version", "4.15.0-42-generic"),
				Tag.of("os_image", "Ubuntu 16.04.5 LTS"),
				Tag.of("provider_id",
						"vsphere://4226652c-5b80-ea02-b8dc" + "-924b2a7a948e"),
				Tag.of("kubelet_version", "v1.11.6"),
				Tag.of("container_runtime_version", "docker://17.12.1-ce"),
				Tag.of("url", "http://localhost:8080/metrics"));

	}

	@Test
	public void all() throws Exception {
		MetricsConverter converter = new MetricsConverter();
		ClassPathResource resource = new ClassPathResource("sample-metrics.dat");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(resource.getInputStream()))) {
			String line = reader.readLine();
			do {
				line = reader.readLine();
			}
			while (line != null);
		}
	}
}