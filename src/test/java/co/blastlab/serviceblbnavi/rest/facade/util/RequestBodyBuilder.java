package co.blastlab.serviceblbnavi.rest.facade.util;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestBodyBuilder {

	private static final Pattern PATTERN = Pattern.compile("\\{(?<name>\\w+)(:(?<def>\\w+))?}");

	private static final String[] AUTO_EXTENSIONS = {"json", "xml", "txt"};

	private final String filename;

	private final Map<String, String> parameters = new HashMap<>();

	public RequestBodyBuilder(String filename) {
		this.filename = filename;
	}

	public RequestBodyBuilder setParameter(String key, String value) {
		parameters.put(key, value);
		return this;
	}

	public RequestBodyBuilder setParameter(String key, Long value) {
		return setParameter(key, value.toString());
	}

	public RequestBodyBuilder setParameter(String key, Integer value) {
		return setParameter(key, value.toString());
	}

	public RequestBodyBuilder setParameter(String key, Double value) {
		return setParameter(key, value.toString());
	}

	public RequestBodyBuilder setParameter(String key, Object value) {
		return setParameter(key, value.toString());
	}

	public String build() {
		String s = readFile();
		Matcher m = PATTERN.matcher(s);

		StringBuffer sb = new StringBuffer(s.length());
		while (m.find()) {
			String name = m.group("name");
			String def = m.group("def");

			if (parameters.get(name) == null && def == null) {
				throw new IllegalStateException("There is no set or default value for \"" + name + "\" parameter.");
			} else if (parameters.get(name) != null) {
				m.appendReplacement(sb, parameters.get(name));
			} else {
				m.appendReplacement(sb, def);
			}
		}
		m.appendTail(sb);

		return sb.toString();
	}

	private String readFile() {
		File file = findFile();

		StringBuilder result = new StringBuilder("");
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
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
