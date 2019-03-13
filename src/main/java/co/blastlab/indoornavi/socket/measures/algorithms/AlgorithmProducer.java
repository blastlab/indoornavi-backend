package co.blastlab.indoornavi.socket.measures.algorithms;


import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class AlgorithmProducer {
	@Produces
	@AlgorithmSelector
	public Algorithm getAlgorithm(@Any Instance<Algorithm> instance) {
		Map<String, String> env = System.getenv();
		if (env.containsKey("ALGORITHM")) {
			Class<? extends Algorithm> algorithmClass;
			switch (env.get("ALGORITHM")) {
				case "GeoN2d":
					algorithmClass = GeoN2d.class;
					break;
				case "GeoN3d":
					algorithmClass = GeoN3d.class;
					break;
				case "Taylor":
					algorithmClass = Taylor.class;
					break;
				default:
					throw new RuntimeException("Unknown ALGORITHM type");
			}
			return instance.select(algorithmClass).get();
		} else {
			throw new RuntimeException("No ALGORITHM in environment variables");
		}
	}
}
