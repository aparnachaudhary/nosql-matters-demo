package net.arunoday.demo.service.impl.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

/**
 * {@link LobHandler} implementation for PostgreSQL.
 * 
 * @author Aparna
 * 
 */
public class PostgresLobHandler extends DefaultLobHandler {

	private NativeJdbcExtractor nativeJdbcExtractor;

	@Override
	public LobCreator getLobCreator() {
		return new PostgresLobCreator();
	}

	public void setNativeJdbcExtractor(NativeJdbcExtractor nativeJdbcExtractor) {
		this.nativeJdbcExtractor = nativeJdbcExtractor;
	}

	@Override
	public InputStream getBlobAsBinaryStream(ResultSet rs, int columnIndex) throws SQLException {

		InputStream is = null;
		try {
			PGConnection pgConn = (PGConnection) getPGConnection(rs.getStatement());
			if (pgConn != null) {
				// Get the Large Object Manager to perform operations with
				LargeObjectManager lobj = pgConn.getLargeObjectAPI();

				long oid = rs.getLong(columnIndex);
				LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
				is = obj.getInputStream();
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error", e);
		}
		return is;
	}

	protected class PostgresLobCreator extends DefaultLobCreator {

		@Override
		public void setBlobAsBinaryStream(PreparedStatement ps, int paramIndex, InputStream contentStream,
				int contentLength) throws SQLException {

			OutputStream outputStream = null;
			try {
				// All LargeObject API calls must be within a transaction block
				PGConnection pgConn = (PGConnection) getPGConnection(ps);
				if (pgConn != null) {
					// Get the Large Object Manager to perform operations with
					LargeObjectManager largeObjectManager = pgConn.getLargeObjectAPI();

					// Create a new large object
					long oid = largeObjectManager.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);

					// Open the large object for writing
					LargeObject largeObject = largeObjectManager.open(oid, LargeObjectManager.WRITE);

					outputStream = largeObject.getOutputStream();

					IOUtils.copy(contentStream, outputStream);

					// Close the large object
					largeObject.close();
					ps.setLong(paramIndex, oid);
				}
			} catch (ClassNotFoundException e) {
				PostgresLobHandler.this.logger.error("Error", e);
			} catch (IOException ex) {
				PostgresLobHandler.this.logger.error("Error", ex);
			} finally {
				IOUtils.closeQuietly(outputStream);
			}

		}

	}

	/**
	 * Retrieve the underlying PGConnection, using a NativeJdbcExtractor if set.
	 */
	protected Connection getPGConnection(Statement ps) throws SQLException, ClassNotFoundException {
		return (nativeJdbcExtractor != null) ? nativeJdbcExtractor.getNativeConnectionFromStatement(ps) : ps
				.getConnection();
	}
}
