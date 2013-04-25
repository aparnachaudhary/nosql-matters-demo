package net.arunoday.demo.model;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Document request
 * 
 * @author Aparna Chaudhary
 * 
 */
public class DocumentRequest implements Serializable {

	private static final long serialVersionUID = 836385307529101518L;

	private String documentType;
	private String filename;
	private String contentType;
	private InputStream content;
	private DocumentMetadata metadata;
	private String docKey;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}

	public DocumentMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(DocumentMetadata metadata) {
		this.metadata = metadata;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	@Override
	public String toString() {
		return "DocumentRequest [filename=" + filename + ", contentType=" + contentType + ", content=" + content
				+ ", metadata=" + metadata + ", documentType=" + documentType + ", docKey=" + docKey + "]";
	}

}
