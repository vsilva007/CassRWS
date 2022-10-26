package ad.cass.rws;

import ad.cass.entity.Test;
import ad.cass.factory.RWSFactory;
import ad.cass.session.TestBeanRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@Path("/test/")
public class TestRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private TestBeanRemote testService = null;


	public TestRWS() {
		factory = RWSFactory.getInstance(); // Get the RWSFactory
	}

	@HEAD()
	@Path("test")
	public Response availabilityTest() {
		return Response.status(Status.OK).header("Message", this.getClass().getName() + " AVAILABLE").build();
	}
	
	/*
	 * @GET()
	 * 
	 * @Path("encrypt/{data}")
	 * 
	 * @Produces(MediaType.TEXT_HTML) //@RolesAllowed({ Permission.ROLE_CONSULTA,
	 * Permission.ROLE_ADMIN }) public Response encryptData(@PathParam("data")
	 * String data,
	 * 
	 * @Context SecurityContext securityContext, @Context HttpServletRequest hsreq)
	 * { System.out.println("encryptData("+data+")"); // Get EJBs: if (factory ==
	 * null) return Response.status(Status.SERVICE_UNAVAILABLE).header("Message",
	 * "Web service initialization error").build(); testService =
	 * factory.getTestService(); if (testService == null) return
	 * Response.status(Status.SERVICE_UNAVAILABLE).header("Message",
	 * "Business logic unavailable").build();
	 * 
	 * try { //LOGIC return Response.ok(testService.encryptDESede(data)).build(); }
	 * catch (Exception ex) { _log.error(ex.getStackTrace()); return
	 * Response.status(Status.INTERNAL_SERVER_ERROR).header("Message",
	 * ex.getMessage()).build(); } }
	 */

	@POST()
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response addTest(Test test, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addTest()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		testService = factory.getTestService();
		if (testService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			testService.addTest(test);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ex.getStackTrace());
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
	
	@GET()
	@Path("list")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response getTests(@Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("getTests()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		testService = factory.getTestService();
		if (testService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Test[] tests = testService.getTests();
			return Response.ok(tests).build();
		} catch (Exception ex) {
			_log.error(ex.getStackTrace());
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
