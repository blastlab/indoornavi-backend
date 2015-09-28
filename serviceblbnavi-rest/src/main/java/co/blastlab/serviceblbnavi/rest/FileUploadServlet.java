package co.blastlab.serviceblbnavi.rest;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.CORSFilter;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.ejb.EJB;
import javax.inject.Inject;
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
@Api(value = "/floor/image/")
public class FileUploadServlet extends HttpServlet {

    @EJB
    private FloorBean floorBean;

    @EJB
    private PersonBean personBean;

    @Inject
    private AuthorizationBean authorizationBean;

    private static final Integer SEPARATOR_INDEX = 1;

    @Override
    @ApiOperation(httpMethod = "POST", value = "upload map")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "floor", value = "floor number", required = true, dataType = "Long", paramType = "form"),
        @ApiImplicitParam(name = "image", value = "map image", required = true, dataType = "Part", paramType = "form")
    })
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (authorize(request, response)) {
            response.setContentType("text/html;charset=UTF-8");

            final Long floorId = Long.parseLong(request.getParameter("floor"));
            final Part filePart = request.getPart("image");

            byte[] bytes = IOUtils.toByteArray(filePart.getInputStream());

            Floor floor = floorBean.find(floorId);
            floor.setBitmap(bytes);
            floorBean.update(floor);
        }

    }

    @Override
    @ApiOperation(httpMethod = "GET", value = "download map")
    @ApiImplicitParam(value = "floor number", required = true, dataType = "Long", paramType = "path")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (authorize(req, resp)) {
            Long floorId;
            String path = req.getPathInfo();
            try {
                floorId = Long.parseLong(path.substring(SEPARATOR_INDEX));
            } catch (NumberFormatException e) {
                throw new BadRequestException();
            }
            Floor floor = floorBean.find(floorId);
            if (floor == null || floor.getBitmap() == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            IOUtils.copy(new ByteArrayInputStream(Base64.getEncoder().encode(floor.getBitmap())), resp.getOutputStream());
        }
    }

    private boolean authorize(HttpServletRequest request, HttpServletResponse response) {
        String authToken = request.getHeader("auth_token");
        if (authToken != null) {
            Person person = personBean.findByAuthToken(authToken);
            if (person != null) {
                authorizationBean.setCurrentUser(person);
                return true;
            }
        }
        response.setStatus(CORSFilter.UNAUTHORIZED);
        return false;
    }

}