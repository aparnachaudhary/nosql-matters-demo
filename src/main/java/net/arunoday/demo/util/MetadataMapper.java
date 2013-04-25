package net.arunoday.demo.util;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSON mapper to convert metadata to/from JSON.
 * 
 * @author Aparna Chaudhary
 * 
 */
public final class MetadataMapper {

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Private constructor.
	 */
	private MetadataMapper() {

	}

	/**
	 * Serializes POJO to JSON.
	 * 
	 * @param input
	 *            input Object to be converted
	 * @return a JSON {@link String} representation
	 * @throws IOException
	 *             when serialization fails
	 */
	public static String toJSON(Object input) throws IOException {
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
		mapper.writeValue(jsonGenerator, input);
		String string = sw.toString();
		return string;
	}

	/**
	 * De-serializes JSON to POJO.
	 * 
	 * @param jsonAsString
	 *            the JSON String
	 * @param pojoClass
	 *            the POJO
	 * @param <T>
	 *            the type.
	 * @return the reconstructed object.
	 * @throws JsonMappingException
	 *             when de-serialization fails
	 * @throws JsonParseException
	 *             when de-serialization fails
	 * @throws IOException
	 *             when de-serialization fails
	 */
	public static <T> Object fromJson(String jsonAsString, Class<T> pojoClass) throws JsonMappingException,
			JsonParseException, IOException {
		return mapper.readValue(jsonAsString, pojoClass);
	}
}
