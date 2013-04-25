package net.arunoday.demo.service.impl.gridfs;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.arunoday.demo.model.DocumentMetadata;
import net.arunoday.demo.model.DocumentRequest;
import net.arunoday.demo.model.FilterCriteria;
import net.arunoday.demo.service.DocumentNotFoundException;
import net.arunoday.demo.service.StorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * GridFS based implementation of {@link StorageService}.
 * 
 * @author Aparna Chaudhary
 */
@Repository("gridFSStorageService")
public class GridFSStorageService implements StorageService {

	private static final Logger logger = LoggerFactory.getLogger(GridFSStorageService.class);

	private static final String GRIDS_KEY_FIELD = "metadata.".concat(DOC_KEY);
	private static final String FILES_COLLECTION = ".files";

	@Autowired
	private MongoDbFactory mongoDbFactory;

	@Autowired
	private MongoConverter mongoConverter;

	@Override
	public DocumentRequest retrieveContent(String documentType, String docKey) throws DocumentNotFoundException {
		long lStartTime = System.currentTimeMillis();

		GridFsTemplate template = new GridFsTemplate(mongoDbFactory, mongoConverter, documentType);
		GridFSDBFile gfsFile = template.findOne(query(where(GRIDS_KEY_FIELD).is(docKey)));

		if (gfsFile == null) {
			throw new DocumentNotFoundException(String.format(
					"Document content is missing for document type %s and id %s", documentType, docKey));
		}
		DocumentRequest docRequest = new DocumentRequest();
		docRequest.setContent(gfsFile.getInputStream());
		docRequest.setFilename(gfsFile.getFilename());

		long lElapsedTime = System.currentTimeMillis() - lStartTime;
		logger.info(String.format("GridFS 'getById' took %s msec ", lElapsedTime));
		return docRequest;
	}

	@Override
	public void save(DocumentRequest docRequest) {
		long lStartTime = System.currentTimeMillis();

		// create unique index on files collection, if non existent
		DBCollection filesCollection = mongoDbFactory.getDb().getCollectionFromString(
				docRequest.getDocumentType() + FILES_COLLECTION);
		BasicDBObject keys = new BasicDBObject(GRIDS_KEY_FIELD, 1);
		filesCollection.ensureIndex(keys, "PKEY", true);

		// store
		Map<String, Object> metadata = docRequest.getMetadata().getAttributes();
		metadata.put(DOC_KEY, docRequest.getDocKey());

		GridFsTemplate template = new GridFsTemplate(mongoDbFactory, mongoConverter, docRequest.getDocumentType());
		template.store(docRequest.getContent(), docRequest.getFilename(), docRequest.getContentType(), metadata);

		long lElapsedTime = System.currentTimeMillis() - lStartTime;
		logger.info(String.format("GridFS 'save' operation took %s msec ", lElapsedTime));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<DocumentRequest> listDocuments(String documentType, List<FilterCriteria> filterCriteria) {

		// prepare criteria
		Query query = new Query();
		for (FilterCriteria filterCriterion : filterCriteria) {
			query = query.addCriteria(whereMetaData(filterCriterion.getName()).is(filterCriterion.getValue()));
		}

		// get the template
		GridFsTemplate template = new GridFsTemplate(mongoDbFactory, mongoConverter, documentType);
		long lStartTime = System.currentTimeMillis();
		// query
		List<GridFSDBFile> files = template.find(query);

		logger.debug(String.format("Total GridFS files %s , fetch time %s msec", files.size(),
				System.currentTimeMillis() - lStartTime));

		ArrayList<DocumentRequest> docRequests = new ArrayList<DocumentRequest>();
		DocumentRequest docRequest = null;

		for (GridFSDBFile file : files) {
			// map metadata
			DocumentMetadata metadata = new DocumentMetadata();
			metadata.setAttributes(file.getMetaData().toMap());

			docRequest = new DocumentRequest();
			docRequest.setDocKey((String) file.getMetaData().get(DOC_KEY));
			docRequest.setFilename(file.getFilename());
			docRequest.setDocumentType(documentType);
			docRequest.setContentType(file.getContentType());
			docRequest.setMetadata(metadata);
			docRequests.add(docRequest);
		}

		long lElapsedTime = System.currentTimeMillis() - lStartTime;
		logger.info(String.format("GridFS 'list' operation took %s msec ", lElapsedTime));
		return docRequests;
	}
}
