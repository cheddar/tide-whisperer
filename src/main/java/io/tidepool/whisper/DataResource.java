package io.tidepool.whisper;

import com.google.common.collect.Iterators;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataResource
{
  private static final Logger log = LoggerFactory.getLogger(DataResource.class);

  private final DBCollection coll;
  private final GatekeeperClient gatekeeper;
  private final SeagullClient seagull;
  private final UserApiClient userApi;

  public DataResource(
      DB db,
      GatekeeperClient gatekeeper,
      SeagullClient seagull,
      UserApiClient userApi
  )
  {
    this.gatekeeper = gatekeeper;
    this.seagull = seagull;
    this.userApi = userApi;

    this.coll = db.getCollection("deviceData");
  }

  @GET
  @Path("/{userId}")
  public Iterator<Map> getData(@PathParam("userId") String id, @Context HttpHeaders hh) {

    Map userInfo = userApi.checkToken(hh.getRequestHeader("x-tidepool-session-token").get(0));

    if (userInfo == null) {
      log.info("No user info");
      return null;
    }

    String presentUser = userInfo.get("userid").toString();
    if (! gatekeeper.canViewData(presentUser, id)) {
      log.info("Cannot view data, user[%s], group[%s]", presentUser, id);
      return null;
    }

    Map privatePair = seagull.getPrivatePair(id, "uploads", userApi.getToken());

    return Iterators.transform(
        coll.find(new BasicDBObject("groupId", privatePair.get("id").toString()))
            .sort(new BasicDBObject("deviceTime", "asc"))
            .iterator(),
        (dbObject) -> {
          Map retVal = dbObject.toMap();
          retVal.remove("groupId");
          return retVal;
        }
    );
  }
}
