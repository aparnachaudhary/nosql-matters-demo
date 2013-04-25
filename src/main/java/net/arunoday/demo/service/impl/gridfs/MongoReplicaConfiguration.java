package net.arunoday.demo.service.impl.gridfs;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * MongoDB replication configurations
 * 
 * @author Aparna
 * 
 */
// @Configuration
public class MongoReplicaConfiguration {

	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		final Mongo mongo = new Mongo(new MongoURI("mongodb://127.0.0.1:27017,127.0.0.1:27018,127.0.0.1:27019"));
		mongo.setWriteConcern(WriteConcern.REPLICAS_SAFE);
		mongo.setReadPreference(ReadPreference.primaryPreferred());
		return new SimpleMongoDbFactory(mongo, "demo");
	}

}
