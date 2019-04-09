package co.blastlab.indoornavi.rest.facade.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class RequestBodyBuilder {

	private static final String[] AUTO_EXTENSIONS = {"json", "xml", "txt"};

	private final String filename;

	private boolean isArray;

	private final Table<Integer, String, Supplier<Object>> parameters = HashBasedTable.create();

	public RequestBodyBuilder(String filename) {
		this.filename = filename;
	}

	public RequestBodyBuilder setParameter(String key, Object value, Integer i) {
		parameters.put(i, key, () -> value);
		return this;
	}

	public RequestBodyBuilder setParameter(String key, Object value) {
		return setParameter(key, value, 0);
	}

	public String build() {
		final List<HashMap<String, Object>> jsonObject;
		try {
			jsonObject = readFileJson();
			parameters.rowKeySet().forEach((i) -> {
				parameters.row(i).keySet().forEach((key) -> {
					if (jsonObject.get(i).containsKey(key)) {
						jsonObject.get(i).put(key, parameters.row(i).get(key).get());
					}
				});
			});
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(isArray ? jsonObject : jsonObject.get(0));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<HashMap<String, Object>> readFileJson() throws IOException {
		File file = findFile();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(file);
		if (jsonNode.isArray()) {
			isArray = true;
			return objectMapper.readValue(file, new TypeReference<List<HashMap<String,Object>>>(){});
		} else {
			return ImmutableList.of(objectMapper.readValue(file, new TypeReference<HashMap<String, Object>>() {}));
		}
	}

	private File findFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(filename);

		int i = 0;
		while (resource == null && i < AUTO_EXTENSIONS.length) {
			resource = classLoader.getResource(filename + "." + AUTO_EXTENSIONS[i]);
			i++;
		}

		if (resource == null) {
			throw new IllegalArgumentException("Given file was not found.");
		}

		return new File(resource.getFile());
	}
}
