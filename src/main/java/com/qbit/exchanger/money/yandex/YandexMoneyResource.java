package com.qbit.exchanger.money.yandex;

import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("yandex")
public class YandexMoneyResource {

	private static final String REDIRECT_PATH = "https://localhost:8443/exchanger/";
	private static final String REDIRECT_ROUTE = "test";

	@Inject
	private YandexMoneyService yandexMoneyService;

//	@GET
//	@Path("authorizeUrl")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String getUrl(@QueryParam("mobile") boolean mobile) {
//		return yandexMoneyService.getAuthorizeUri(mobile);
//	}
	
	@GET
	@Path("authorizeUrl")
	@Produces(MediaType.APPLICATION_JSON)
	public AuthorizeUrlWrapper getUrl(@QueryParam("mobile") boolean mobile) {
		AuthorizeUrlWrapper urlWrapper = new AuthorizeUrlWrapper();
		urlWrapper.setUrl(yandexMoneyService.getAuthorizeUri(mobile));
		return urlWrapper;
	}

	@GET
	@Path("proceedAuth")
	public Response proceedAuth(@QueryParam("code") String code, @QueryParam("error") String error) {
		/*
		 * if redirect to route works fine we need to return wallet as query parameter
		 */
		if (code != null) {
			yandexMoneyService.exchangeAndStoreToken(code);
		} else {
			throw new RuntimeException((error != null) ? error : "code is empty!");
		}
		URI uri = UriBuilder.fromPath(REDIRECT_PATH).fragment("{route}").build(REDIRECT_ROUTE);
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("redirect")
	public Response redirect() throws URISyntaxException {
		URI uri = UriBuilder.fromPath("https://localhost:8443/exchanger/").fragment("{route}").build("test?code=asdcvbnm");
		return Response.seeOther(uri).build();
	}
}
