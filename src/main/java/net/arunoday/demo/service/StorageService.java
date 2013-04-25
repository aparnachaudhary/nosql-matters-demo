package net.arunoday.demo.service;

import java.util.ArrayList;
import java.util.List;

import net.arunoday.demo.model.DocumentRequest;
import net.arunoday.demo.model.FilterCriteria;

/**
 * Storage service API.
 * 
 * @author Aparna Chaudhary
 * 
 */
public interface StorageService {

	static final String FILENAME = "filename";
	static final String CONTENT_TYPE = "contentType";
	static final String DOC_KEY = "docKey";
	static final String ID = "id";

	static final String INSERT_CONTENT_SQL = "INSERT INTO health_record_data VALUES (?, ?, ?)";
	static final String GET_CONTENT_SQL = " SELECT data FROM health_record_data WHERE DOCKEY = ?";
	static final String HEALTH_RECORD_TABLE = "health_record";

	/**
	 * Saves document content and metadata based on the request.
	 * 
	 * @param docRequest
	 *            document request
	 */
	void save(DocumentRequest docRequest);

	/**
	 * Get document content by docId.
	 * 
	 * @param documentType
	 *            document type name
	 * @param id
	 *            document id
	 * @return document content
	 * @throws DocumentNotFoundException
	 *             when no document found for docId
	 */
	DocumentRequest retrieveContent(String documentType, String docKey) throws DocumentNotFoundException;

	/**
	 * List documents based on the filter criteria.
	 * 
	 * @param documentType
	 *            document type name
	 * @param filterCriteria
	 *            filter criteria
	 * @return list of documents
	 */
	ArrayList<DocumentRequest> listDocuments(String documentType, List<FilterCriteria> filterCriteria);

}
