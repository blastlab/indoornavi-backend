package co.blastlab.serviceblbnavi.rest;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Michał Koszałka
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/rest/v1/file/upload"})
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

	@EJB
	private FloorBean floorBean;

	@Override
	protected void doPost(HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		final Long floorId = Long.parseLong(request.getParameter("floor"));
		final Part filePart = request.getPart("file");

		byte[] bytes = IOUtils.toByteArray(filePart.getInputStream());

		Floor floor = floorBean.find(floorId);
		floor.setBitmap(bytes);
		floorBean.update(floor);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Long floorId = Long.parseLong(req.getParameter("floor"));

		Floor floor = floorBean.find(floorId);
		if (floor == null) {
			throw new EntityNotFoundException();
		}
		IOUtils.copy(new ByteArrayInputStream(floor.getBitmap()), resp.getOutputStream());

	}

}
