package com.qbit.exchanger.order.resource;

import com.qbit.exchanger.order.dao.OrderBufferTypeDAO;
import com.qbit.exchanger.order.model.OrderBufferType;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Александр
 */
@Path("orderBufferTypes")
public class OrderBufferTypesResource {
	
	@Inject
	private OrderBufferTypeDAO orderBufferTypeDAO;
	
	@GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public OrderBufferType get(@PathParam("id") String id) {
		return orderBufferTypeDAO.find(id);
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public List<OrderBufferType> findAll() {
		return orderBufferTypeDAO.findAll();
	}
}
