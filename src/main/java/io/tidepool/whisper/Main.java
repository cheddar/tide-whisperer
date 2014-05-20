package io.tidepool.whisper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.UnknownHostException;

/**
 *
 */
public class Main extends Application<Config>
{
  public static void main(String[] args) throws Exception
  {
    new Main().run(args);
  }

  private MongoClient mongoClient = null;

  @Override
  public String getName()
  {
    return "tide-whisperer";
  }

  @Override
  public void initialize(Bootstrap<Config> configBootstrap)
  {
  }

  @Override
  public void run(Config config, Environment environment) throws Exception
  {
    MongoClientURI uri = new MongoClientURI(config.getMongoConnectionString());
    makeMongoClient(environment, uri);

    final HttpClient http = new HttpClientBuilder(environment).using(config.getHttpClientConfiguration())
                                                         .build("main http client");

    ObjectMapper jackson = environment.getObjectMapper();
    final UserApiClient userApi = new UserApiClient(http, jackson);

    DataResource dataResource = new DataResource(
        mongoClient.getDB(uri.getDatabase()),
        new GatekeeperClient(http, userApi, jackson),
        new SeagullClient(http, jackson),
        userApi
    );

    environment.jersey().register(dataResource);
  }

  private void makeMongoClient(Environment environment, MongoClientURI uri) throws UnknownHostException
  {
    mongoClient = new MongoClient(uri);
    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() throws Exception
      {

      }

      @Override
      public void stop() throws Exception
      {
        mongoClient.close();
      }
    });
  }
}
