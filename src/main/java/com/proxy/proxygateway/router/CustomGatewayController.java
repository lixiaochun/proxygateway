package com.proxy.proxygateway.router;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * The CustomGatewayController provides rest end point
 * 
 * @author Rohan Surve
 * @version 1.0
 * @since 2018-01-20
 */
@Slf4j

@Path("/router")

public class CustomGatewayController {

	@Inject
	public CustomGatewayController() {
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/test")
	public Response getLastName() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("LastName", "TESTED -LASTNAME");
		log.info("jsonObject..." + jsonObject);

		return Response.ok().entity(jsonObject.toString()).build();
	}

}
