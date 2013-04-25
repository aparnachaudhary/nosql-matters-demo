package net.arunoday.demo.service;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.arunoday.demo.model.DocumentRequest;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

/**
 * REST interface for document management.
 * 
 * @author Aparna Chaudhary
 * 
 */
@Path("/")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public interface DocumentService {

	static final String POSTGRES = "postgres";
	static final String ORACLE = "oracle";
	static final String GRIDFS = "gridfs";

	static final String DOCUMENT_METADATA_PART_NAME = "metadata";
	static final String DOCUMENT_KEY_PART_NAME = "docKey";
	static final String CONTENT_PART_NAME = "content";
	static final String FILENAME = "filename";

	/**
	 * Stores content and metadata for the document in the provided storage option.
	 * 
	 * @param input
	 *            multi-part input data
	 * @param storage
	 *            storage option
	 * @param documentType
	 *            document type name
	 * @return response
	 */
	@POST
	@Path("/{storage}/{docType}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_XML)
	Response storeDocument(MultipartInput input, @PathParam("storage") String storage,
			@PathParam("docType") String documentType);

	/**
	 * Retrieves a document from the storage based on the document id.
	 * 
	 * @param storage
	 *            storage option e.g. gridfs, postgres, oracle.
	 * @param documentType
	 *            document type name
	 * @param docKey
	 *            document key
	 * @return document for the docId
	 */
	@GET
	@Path("/get/{storage}/{docType}/{docId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response getDocument(@PathParam("storage") String storage, @PathParam("docType") String documentType,
			@PathParam("docId") String docKey);

	/**
	 * List documents of a document type based on provided filter criteria.
	 * 
	 * @param storage
	 *            storage option e.g. gridfs, postgres, oracle.
	 * @param documentType
	 *            document type name
	 * @param uriInfo
	 *            filter criteria
	 * @return list of documents
	 */
	@GET
	@Path("/list/{storage}/{docType}")
	@Produces(MediaType.APPLICATION_JSON)
	ArrayList<DocumentRequest> listDocuments(@PathParam("storage") String storage,
			@PathParam("docType") String documentType, @Context UriInfo uriInfo);
}
