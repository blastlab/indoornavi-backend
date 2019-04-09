package co.blastlab.indoornavi.socket.measures.algorithms;


import com.google.common.collect.ImmutableMap;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class AlgorithmProducer {
	private final Map<String, Class<? extends Algorithm>> algorithms = ImmutableMap.of("GeoN2d", GeoN2d.class, "GeoN3d", GeoN3d.class, "Taylor", Taylor.class);

	@Produces
	@AlgorithmSelector
	public Algorithm getAlgorithm(@Any Instance<Algorithm> instance) {
		Map<String, String> env = System.getenv();
		if (env.containsKey("ALGORITHM") && algorithms.containsKey(env.get("ALGORITHM"))) {
			return instance.select(algorithms.get(env.get("ALGORITHM"))).get();
		} else {
			throw new UnknownAlgorithmRuntimeException();
		}
	}

	private class UnknownAlgorithmRuntimeException extends RuntimeException {
		public UnknownAlgorithmRuntimeException() {
			super("No ALGORITHM in environment variables");
		}
	}
}
