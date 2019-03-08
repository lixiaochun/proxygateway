package com.proxy.proxygateway.router;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.proxy.proxygateway.context.GatewayProxyRequest;
import com.proxy.proxygateway.context.ProxyContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/")

@Singleton
public class ReverseProxyGatewayRouter {
	
	
	private final ProxyContext context;

	@Inject
	public ReverseProxyGatewayRouter(ProxyContext context) {

		this.context = context;

	}


	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{any: .*}")
	public Response redirectTo(@Context HttpServletRequest req, @Context ContainerRequestContext crc) throws URISyntaxException, IOException {

		log.info("Server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());

		/*
		 * ProxyRequest proxyRequest = new ProxyRequest(req);
		 * 
		 * HttpClient client = HttpClientBuilder.create().build();
		 * 
		 * URIBuilder builder = new URIBuilder().setPath("/nonspring-gateway" +
		 * proxyRequest.getPath()).setScheme("http")
		 * .setHost("localhost").setPort(8086);
		 * 
		 * proxyRequest.getParams().forEach((key, value) ->
		 * builder.addParameter(key, value));
		 * 
		 * HttpGet httpget = new HttpGet(builder.build());
		 * 
		 * 
		 * // Set up the response handler ResponseHandler<String> handler = new
		 * ResponseHandler<String>() {
		 * 
		 * @Override public String handleResponse(final HttpResponse response)
		 * throws ClientProtocolException, IOException {
		 * 
		 * int status = response.getStatusLine().getStatusCode();
		 * 
		 * log.info("Status: " + status); HttpEntity entity =
		 * response.getEntity(); return entity != null
		 * ?IOUtils.toString(response.getEntity().getContent(),
		 * StandardCharsets.UTF_8) : null; } };
		 * 
		 * try { // responseBody = client.execute(httpGet, handler);
		 * 
		 * HttpResponse response = client.execute(httpget);
		 * log.info("STATUS  RESPONSE :" +
		 * response.getStatusLine().getStatusCode()); Header contentType =
		 * response.getFirstHeader(HttpHeaders.CONTENT_TYPE); String
		 * contentTypeValue = contentType != null ?
		 * response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue() : "";
		 * log.info("CONTENT-TYPE     :" + contentTypeValue);
		 * 
		 * String data = EntityUtils.toString(response.getEntity()); //
		 * HttpEntity entity = response.getEntity() ; return
		 * Response.status(response.getStatusLine().getStatusCode()).entity(data
		 * ).build();
		 * 
		 * } catch (ClientProtocolException e) { e.printStackTrace(); } catch
		 * (IOException e) { e.printStackTrace(); } finally {
		 * httpget.releaseConnection(); } return null
		 */;

			return executeProxyHandler(req, crc,this.context.getProxyMap());

	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("anonymous/{any: .*}")
	public Response anonymousGetRedirectTo(@Context HttpServletRequest req ,@Context ContainerRequestContext crc) throws URISyntaxException, IOException {
		log.info("Anonymous Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeAnonymousAllPermitProxyHandler(req, crc,this.context.getProxyAnonymousMap());

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{any: .*}")
	public Response postRedirectTo(@Context HttpServletRequest req, @Context ContainerRequestContext crc)
			throws URISyntaxException, IOException {
		log.info("POST Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeProxyHandler(req, crc,this.context.getProxyMap());

	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("anonymous/{any: .*}")
	public Response anonymousPostRedirectTo(@Context HttpServletRequest req ,@Context ContainerRequestContext crc) throws URISyntaxException, IOException {
		log.info("Anonymous POST Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeAnonymousAllPermitProxyHandler(req, crc,this.context.getProxyAnonymousMap());

	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{any: .*}")
	public Response deleteRedirectTo(@Context HttpServletRequest req, @Context ContainerRequestContext crc)
			throws URISyntaxException, IOException {
		log.info("Delete Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeProxyHandler(req, crc,this.context.getProxyMap());

	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("anonymous/{any: .*}")
	public Response anonymousDeleteRedirectTo(@Context HttpServletRequest req ,@Context ContainerRequestContext crc) throws URISyntaxException, IOException {
		log.info("Anonymous Delete Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeAnonymousAllPermitProxyHandler(req, crc,this.context.getProxyAnonymousMap());

	}
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{any: .*}")
	public Response putRedirectTo(@Context HttpServletRequest req, @Context ContainerRequestContext crc)
			throws URISyntaxException, IOException {
		log.info("PUT Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeProxyHandler(req, crc,this.context.getProxyMap());

	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("anonymous/{any: .*}")
	public Response anonymousPutRedirectTo(@Context HttpServletRequest req ,@Context ContainerRequestContext crc) throws URISyntaxException, IOException {
		log.info("Anonymous Put Call  server address -    {} , port - {}  , servlet.context-path - {} ", req.getLocalAddr(),
				req.getLocalPort(), req.getContextPath());
		return executeAnonymousAllPermitProxyHandler(req, crc,this.context.getProxyAnonymousMap());

	}

	private class ContentLengthHeaderRemover implements HttpRequestInterceptor {
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			request.removeHeaders(HTTP.CONTENT_LEN);
		}
	}

	
	private Response requestForwardingProcess(GatewayProxyRequest proxyRequest ,String domain) throws IOException{
		
		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().addInterceptorFirst(new ContentLengthHeaderRemover())
				.build();

		try {
			HttpEntity stringEntity = new StringEntity(proxyRequest.getContent(), ContentType.APPLICATION_JSON);

			HttpUriRequest request = RequestBuilder.create(proxyRequest.getMethod()).setUri(domain+proxyRequest.getPath()).setEntity(stringEntity).build();

			proxyRequest.getHeaders().forEach((key, value) -> request.addHeader(key, value));

			HttpParams params = new BasicHttpParams();
			proxyRequest.getParams().forEach((key, value) -> params.setParameter(key, value));
			request.setParams(params);
			
			response = client.execute(request);

			return processResponse(response);

		} catch ( IOException e) {
			e.printStackTrace();
		}  finally {
			if (client != null)
				client.close();
		}
		return null;
	}
	
	private Response processResponse(HttpResponse httpResponse) throws ParseException, IOException {

		Response response = Response.status(httpResponse.getStatusLine().getStatusCode()).build();

		if (httpResponse.getEntity() != null) {

			response = Response.status(httpResponse.getStatusLine().getStatusCode())
					.entity(EntityUtils.toString(httpResponse.getEntity())).build();
		}
		for (Header header : httpResponse.getAllHeaders()) {
			response.getHeaders().add(header.getName(), header.getValue());
		}
		return response;
	}
	
	private Response executeAnonymousAllPermitProxyHandler(HttpServletRequest req, ContainerRequestContext crc ,ConcurrentMap<String, Pair<String,String>> proxyMap  ) throws IOException {
		GatewayProxyRequest proxyRequest = new GatewayProxyRequest(req, crc);
		String domain = null;

		for (Map.Entry<String, Pair<String, String>>  entry : proxyMap.entrySet()) {

			if (proxyRequest.getPath().contains(entry.getKey())) {
				domain = entry.getValue().getRight();
			}

		}
		
        return requestForwardingProcess( proxyRequest , domain);
	}
	
	
	private Response executeProxyHandler(HttpServletRequest req, ContainerRequestContext crc, ConcurrentMap<String, String> proxyMap ) throws IOException {
		GatewayProxyRequest proxyRequest = new GatewayProxyRequest(req, crc);
		String domain = null;

		for (Map.Entry<String, String> entry : proxyMap.entrySet()) {

			if (proxyRequest.getPath().contains(entry.getKey())) {
				domain = entry.getValue();
			}

		}
		
		return requestForwardingProcess( proxyRequest , domain);

		/*HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().addInterceptorFirst(new ContentLengthHeaderRemover())
				.build();

		try {
			HttpEntity stringEntity = new StringEntity(proxyRequest.getContent(), ContentType.APPLICATION_JSON);

			HttpUriRequest request = RequestBuilder.create(proxyRequest.getMethod()).setUri(domain+proxyRequest.getPath()).setEntity(stringEntity).build();

			proxyRequest.getHeaders().forEach((key, value) -> request.addHeader(key, value));

			HttpParams params = new BasicHttpParams();
			proxyRequest.getParams().forEach((key, value) -> params.setParameter(key, value));
			request.setParams(params);
			
			response = client.execute(request);

			return processResponse(response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return null;*/
	}
	
	/*
	private Response executeProxyHandler1(HttpServletRequest req, ContainerRequestContext crc) throws IOException {
		GatewayProxyRequest proxyRequest = new GatewayProxyRequest(req, crc);
		String path = null;

		for (Map.Entry<String, String> entry : proxyMap.entrySet()) {

			if (proxyRequest.getPath().contains(entry.getKey())) {
				path = entry.getValue();
			}

		}

		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().addInterceptorFirst(new ContentLengthHeaderRemover())
				.build();

		try {
			HttpEntity stringEntity = new StringEntity(proxyRequest.getContent(), ContentType.APPLICATION_JSON);

			HttpUriRequest request = RequestBuilder.create("POST").setUri(path).setEntity(stringEntity).build();

		//	proxyRequest.getHeaders().forEach((key, value) -> request.addHeader(key, value));

			HttpParams params = new BasicHttpParams();
			proxyRequest.getParams().forEach((key, value) -> params.setParameter(key, value));
			request.setParams(params);
			response = client.execute(request);

			return processResponse(response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
		return null;
	}

	private Response executeProxyHandler2(HttpServletRequest req, ContainerRequestContext crc) {
		GatewayProxyRequest proxyRequest = new GatewayProxyRequest(req, crc);
		String path = null;

		for (Map.Entry<String, String> entry : proxyMap.entrySet()) {

			if (proxyRequest.getPath().contains(entry.getKey())) {
				path = entry.getValue();
			}

		}

		HttpClient client = HttpClientBuilder.create().addInterceptorFirst(new ContentLengthHeaderRemover()).build();

		URIBuilder builder = new URIBuilder().setPath(path).setScheme("http").setHost("localhost").setPort(8090);

		proxyRequest.getParams().forEach((key, value) -> builder.addParameter(key, value));

		HttpGet httpget = null;
		HttpResponse response = null;
		try {
			httpget = new HttpGet(builder.build());

			// HttpResponse response = client.execute(httpget);
			HttpPost post = new HttpPost(builder.build());
		//	proxyRequest.getHeaders().forEach((key, value) -> post.setHeader(key, value));

			post.setHeader("Content-type", "application/json");
			try {
				StringEntity stringEntity = new StringEntity(proxyRequest.getContent());
				// post.getRequestLine();
				post.setEntity(stringEntity);
				response = client.execute(post);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return processResponse(response);

		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpget != null)
				httpget.releaseConnection();
		}
		return null;
	}*/

	

}
