package co.blastlab.serviceblbnavi.rest;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.domain.Floor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Michał Koszałka
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/rest/v1/floor/image/*"})
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

	@EJB
	private FloorBean floorBean;

	private static final Integer SEPARATOR_INDEX = 1;

	@Override
	protected void doPost(HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		final Long floorId = Long.parseLong(request.getParameter("floor"));
		final Part filePart = request.getPart("image");

		byte[] bytes = IOUtils.toByteArray(filePart.getInputStream());

		Floor floor = floorBean.find(floorId);
		floor.setBitmap(bytes);
		floorBean.update(floor);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long floorId;
		String path = req.getPathInfo();
		try {
			floorId = Long.parseLong(path.substring(SEPARATOR_INDEX));
		} catch (NumberFormatException e) {
			throw new BadRequestException();
		}
		Floor floor = floorBean.find(floorId);
		if (floor == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		IOUtils.copy(new ByteArrayInputStream(Base64.getEncoder().encode(floor.getBitmap())), resp.getOutputStream());

	}

}
