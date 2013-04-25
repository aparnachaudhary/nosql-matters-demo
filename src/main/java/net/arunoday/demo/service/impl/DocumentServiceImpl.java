package net.arunoday.demo.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import net.arunoday.demo.model.DocumentMetadata;
import net.arunoday.demo.model.DocumentRequest;
import net.arunoday.demo.model.FilterCriteria;
import net.arunoday.demo.service.DocumentNotFoundException;
import net.arunoday.demo.service.DocumentService;
import net.arunoday.demo.service.impl.gridfs.GridFSStorageService;
import net.arunoday.demo.service.impl.jdbc.OracleStorageService;
import net.arunoday.demo.service.impl.jdbc.PostgresStorageService;
import net.arunoday.demo.util.MetadataMapper;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for {@link DocumentService}
 * 
 * @author Aparna Chaudhary
 * 
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Autowired
	private GridFSStorageService gridFSStorageService;

	@Autowired
	private PostgresStorageService postgresStorageService;

	@Autowired
	private OracleStorageService oracleStorageService;

	@Override
	public Response storeDocument(MultipartInput multipart, String storage, String documentType) {
		try {
			List<InputPart> inputParts = multipart.getParts();
			if (inputParts == null || inputParts.size() != 3) {
				logger.error("Request does not contain valid input parts.");
				return Response.status(Status.BAD_REQUEST).build();
			}
			try {
				DocumentRequest request = decode(multipart);
				request.setDocumentType(documentType);
				if (storage.equalsIgnoreCase(GRIDFS)) {
					gridFSStorageService.save(request);
				} else if (storage.equalsIgnoreCase(ORACLE)) {
					oracleStorageService.save(request);
				} else if (storage.equalsIgnoreCase(POSTGRES)) {
					postgresStorageService.save(request);
				}
			} catch (IOException e) {
				logger.error("Cannot decode multipart input.", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
			return Response.ok().build();
		} finally {
			multipart.close();
		}
	}

	@Override
	public Response getDocument(String storage, String documentType, String docKey) {
		try {
			DocumentRequest docRequest = null;
			if (storage.equalsIgnoreCase(GRIDFS)) {
				docRequest = gridFSStorageService.retrieveContent(documentType, docKey);
			} else if (storage.equalsIgnoreCase(POSTGRES)) {
				docRequest = postgresStorageService.retrieveContent(documentType, docKey);
			} else if (storage.equalsIgnoreCase(ORACLE)) {
				docRequest = oracleStorageService.retrieveContent(documentType, docKey);
			} else {
				throw new UnsupportedOperationException(String.format("GET operation for %s storage", storage));
			}
			return Response.ok(docRequest.getContent(), MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment;filename=" + docRequest.getFilename()).build();
		} catch (DocumentNotFoundException e) {
			logger.error(String.format("Document not found for id %s", docKey), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ArrayList<DocumentRequest> listDocuments(String storage, String documentType, UriInfo uriInfo) {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		List<FilterCriteria> filterCriteria = new ArrayList<FilterCriteria>();

		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			filterCriteria.add(new FilterCriteria(entry.getKey(), entry.getValue().get(0)));
			logger.debug("Filter criteria is: " + new FilterCriteria(entry.getKey(), entry.getValue().get(0)));
		}

		ArrayList<DocumentRequest> docRequests = new ArrayList<DocumentRequest>();
		if (storage.equalsIgnoreCase(GRIDFS)) {
			docRequests = gridFSStorageService.listDocuments(documentType, filterCriteria);
		} else if (storage.equalsIgnoreCase(POSTGRES)) {
			docRequests = postgresStorageService.listDocuments(documentType, filterCriteria);
		} else if (storage.equalsIgnoreCase(ORACLE)) {
			docRequests = oracleStorageService.listDocuments(documentType, filterCriteria);
		} else {
			throw new UnsupportedOperationException(String.format("LIST operation for %s storage", storage));
		}
		return docRequests;
	}

	// //////////////////////
	// Helper methods
	// //////////////////////
	private DocumentRequest decode(MultipartInput input) throws IOException {
		String contentDispositionHeader = "Content-Disposition";

		List<InputPart> inputParts = input.getParts();
		DocumentRequest request = new DocumentRequest();

		for (InputPart inputPart : inputParts) {
			MultivaluedMap<String, String> headers = inputPart.getHeaders();

			if (StringUtils.contains(headers.getFirst(contentDispositionHeader), DOCUMENT_METADATA_PART_NAME)) {
				String jsonDoc = inputPart.getBodyAsString();
				DocumentMetadata metadata = (DocumentMetadata) MetadataMapper.fromJson(jsonDoc, DocumentMetadata.class);
				request.setMetadata(metadata);
				continue;
			}
			if (StringUtils.contains(headers.getFirst(contentDispositionHeader), DOCUMENT_KEY_PART_NAME)) {
				String docKey = inputPart.getBodyAsString();
				request.setDocKey(docKey);
				continue;
			}

			if (StringUtils.contains(headers.getFirst(contentDispositionHeader), CONTENT_PART_NAME)) {
				String filename = headers.getFirst(FILENAME) != null ? headers.getFirst(FILENAME) : getFileName(headers
						.getFirst(contentDispositionHeader));
				request.setFilename(filename);
				request.setContentType(MediaType.APPLICATION_XML);
				request.setContent(inputPart.getBody(InputStream.class, null));
				continue;
			}
		}
		logger.debug("Extracted document request" + request);
		return request;
	}

	// //////////////////////
	// Helper methods
	// //////////////////////
	private String getFileName(String contentDisposition) {
		for (String filename : contentDisposition.split(";")) {
			if ((filename.trim().startsWith(FILENAME))) {
				String[] name = filename.split("=");
				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return null;
	}

}
