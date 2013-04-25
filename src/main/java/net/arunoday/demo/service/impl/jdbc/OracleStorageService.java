package net.arunoday.demo.service.impl.jdbc;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.arunoday.demo.model.DocumentMetadata;
import net.arunoday.demo.model.DocumentRequest;
import net.arunoday.demo.model.FilterCriteria;
import net.arunoday.demo.service.StorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.stereotype.Service;

/**
 * Oracle implementation of {@link StorageService}
 * 
 * @author Aparna
 */
@Service("oracleStorageService")
public class OracleStorageService extends JdbcDaoSupport implements StorageService {

	private static final Logger logger = LoggerFactory.getLogger(OracleStorageService.class);

	@Autowired
	@Qualifier("oracleLobHandler")
	private OracleLobHandler lobHandler;

	@Autowired
	@Qualifier("oracleHealthRecordIncrementer")
	private DataFieldMaxValueIncrementer healthRecordId;

	@Autowired
	@Qualifier("oracleHealthRecordDataIncrementer")
	private DataFieldMaxValueIncrementer healthRecordDataId;

	@Override
	public void save(final DocumentRequest docRequest) {
		long lStartTime = System.currentTimeMillis();
		final long dataId = healthRecordDataId.nextLongValue();
		getJdbcTemplate().update(INSERT_CONTENT_SQL, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, dataId);
				ps.setString(2, docRequest.getDocKey());
				lobHandler.getLobCreator().setBlobAsBinaryStream(ps, 3, docRequest.getContent(), -1);
			}
		});

		final SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(getJdbcTemplate()).withTableName(HEALTH_RECORD_TABLE);

		Map<String, Object> parameters = new HashMap<String, Object>();
		// set fixed parameters
		parameters.put(ID, healthRecordId.nextLongValue());
		parameters.put(DOC_KEY, docRequest.getDocKey());
		parameters.put(CONTENT_TYPE, docRequest.getContentType());
		parameters.put(FILENAME, docRequest.getFilename());

		// set variable metadata
		for (Entry<String, Object> attribute : docRequest.getMetadata().getAttributes().entrySet()) {
			parameters.put(attribute.getKey(), attribute.getValue());
		}

		jdbcInsert.execute(parameters);

		long lElapsedTime = System.currentTimeMillis() - lStartTime;
		logger.info(String.format("Oracle 'store' took %s msec ", lElapsedTime));
	}

	@Override
	public DocumentRequest retrieveContent(String documentType, String docKey) {
		long lStartTime = System.currentTimeMillis();
		InputStream content = getJdbcTemplate().queryForObject(GET_CONTENT_SQL, new Object[] { docKey },
				new FileMapper());

		DocumentRequest docRequest = new DocumentRequest();
		docRequest.setContent(content);
		docRequest.setDocumentType(documentType);
		docRequest.setDocKey(docKey);
		long lElapsedTime = System.currentTimeMillis() - lStartTime;
		logger.info(String.format("Oracle 'getById' for docKey %s took %s msec ", docKey, lElapsedTime));
		return docRequest;
	}

	@Override
	public ArrayList<DocumentRequest> listDocuments(String documentType, List<FilterCriteria> filterCriteria) {
		long lStartTime = System.currentTimeMillis();
		String query = "SELECT * FROM " + HEALTH_RECORD_TABLE + " WHERE 1=1";

		Object[] input = new Object[filterCriteria.size()];
		int i = 0;
		for (FilterCriteria filterCriterion : filterCriteria) {
			query += " AND " + filterCriterion.getName() + " = ?";
			input[i] = filterCriterion.getValue();
			i++;
		}

		List<Map<String, Object>> results = getJdbcTemplate().queryForList(query, input);

		ArrayList<DocumentRequest> docRequests = new ArrayList<DocumentRequest>();
		for (Map<String, Object> result : results) {

			DocumentMetadata metadata = new DocumentMetadata();
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("field1", result.get("field1"));
			attributes.put("field2", result.get("field2"));
			attributes.put("field3", result.get("field3"));
			metadata.setAttributes(attributes);

			DocumentRequest docRequest = new DocumentRequest();
			docRequest.setDocKey((String) result.get(DOC_KEY));
			docRequest.setContentType((String) result.get(CONTENT_TYPE));
			docRequest.setFilename((String) result.get(FILENAME));
			docRequest.setDocumentType(documentType);
			docRequest.setMetadata(metadata);

			docRequests.add(docRequest);
		}
		long lElapsedTime = System.currentTimeMillis() - lStartTime;
		logger.info(String.format("Oracle 'list' took %s msec ", lElapsedTime));

		return docRequests;
	}

	/**
	 * Maps binary content to {@link InputStream}.
	 * 
	 * @author Aparna
	 * 
	 */
	class FileMapper implements ParameterizedRowMapper<InputStream> {
		@Override
		public InputStream mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getBinaryStream(1);
		}
	}

}
