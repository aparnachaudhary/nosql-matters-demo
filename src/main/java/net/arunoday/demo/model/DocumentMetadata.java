/**
 * 
 */
package net.arunoday.demo.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Document metadata
 * 
 * @author Aparna Chaudhary
 * 
 */
public class DocumentMetadata {

	private Map<String, Object> attributes = new HashMap<String, Object>();

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "DocumentMetadata [attributes=" + attributes + "]";
	}

}
