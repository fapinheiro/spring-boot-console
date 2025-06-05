package labs.pinheiro.springbootconsole.arearestrita.config;


import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;



@Configuration
@EnableMongoRepositories(basePackages = {"labs.pinheiro.springbootconsole.arearestrita"})
public class MongoConfig extends AbstractMongoClientConfiguration  {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }
 
    @Bean
    public MongoTemplate mongoTemplate()
        throws UnknownHostException, java.net.UnknownHostException {
        return new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(
                            mongoClient(),
                            getDatabaseName()
                    )
            );
    }

    @Override
    public MongoClient mongoClient() {

        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
 
    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("br.com.embracon.usecase.cotas");
    }

   
  
   

    
    
}
