package am.ik.pks.wavefront;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@Configuration
public class DaemonHandler {
	private final MeterRegistry meterRegistry;
	private final MetricsConverter converter = new MetricsConverter();

	public DaemonHandler(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Bean
	public RouterFunction<ServerResponse> routes() {
		return RouterFunctions.route() //
				.POST("/daemon/{agentId}/pushdata/{workUnitId}", this::pushData) //
				.POST("/daemon/{agentId}/checkin", this::dummy) //
				.POST("/daemon/{agentId}/config/processed", this::dummy) //
				.POST("/daemon/{agentId}/error", this::dummy) //
				.build();
	}

	public Mono<ServerResponse> pushData(ServerRequest req) {
		return req.bodyToMono(byte[].class).flatMap(body -> {
			try (GZIPInputStream inputStream = new GZIPInputStream(
					new ByteArrayInputStream(body))) {
				String all = StreamUtils.copyToString(inputStream,
						StandardCharsets.UTF_8);
				String[] lines = all.split("\n");
				for (String line : lines) {
					GaugeMetrics gaugeMetrics = this.converter.convert(line.trim());
					gaugeMetrics.register(this.meterRegistry);
				}
			}
			catch (IOException e) {
				return Mono.error(e);
			}
			return ServerResponse.ok() //
					.contentType(APPLICATION_STREAM_JSON) //
					.syncBody("{}");
		});
	}

	public Mono<ServerResponse> dummy(ServerRequest req) {
		return ServerResponse.ok() //
				.contentType(APPLICATION_STREAM_JSON) //
				.syncBody("{}");
	}
}
