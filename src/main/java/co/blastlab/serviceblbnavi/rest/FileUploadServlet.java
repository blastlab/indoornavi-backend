package co.blastlab.serviceblbnavi.rest;

import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.dao.exception.PermissionException;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.CORSFilter;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 *
 * @author Michał Koszałka
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/rest/v1/floor/image/*"})
@MultipartConfig
@Api(value = "/floor/image/")
public class FileUploadServlet extends HttpServlet {

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;

    @Inject
    private FloorBean floorBean;

    private static final Integer SEPARATOR_INDEX = 1;

    @Override
    @ApiOperation(httpMethod = "POST", value = "upload map")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "floor", value = "floor number", required = true, dataType = "Long", paramType = "form"),
        @ApiImplicitParam(name = "image", value = "map image", required = true, dataType = "Part", paramType = "form")
    })
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            authorize(request);
            response.setContentType("text/html;charset=UTF-8");

            //Floor floor = floorBean.find(Long.parseLong(request.getParameter("floor")));
            Floor floor = floorRepository.findBy(Long.parseLong(request.getParameter("floor")));
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    floor.getBuilding().getComplex().getId(), Permission.UPDATE);
            final Part filePart = request.getPart("image");
            
            byte[] bytes = IOUtils.toByteArray(filePart.getInputStream());
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
            
            floor.setBitmapHeight(bi.getHeight());
            floor.setBitmapWidth(bi.getWidth());
            floor.setBitmap(bytes);
            //floorBean.update(floor);
            floorBean.save(floor);
        } catch (PermissionException e) {
            response.setStatus(CORSFilter.UNAUTHORIZED);
        } catch (NumberFormatException e) {
            response.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Override
    @ApiOperation(httpMethod = "GET", value = "download map")
    @ApiImplicitParam(value = "floor number", required = true, dataType = "Long", paramType = "path")
    protected void doGet(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            /*Floor floor = floorBean.find(Long.parseLong(
                    req.getPathInfo().substring(SEPARATOR_INDEX))
            );*/
            Floor floor = floorRepository.findBy(Long.parseLong(
                    req.getPathInfo().substring(SEPARATOR_INDEX))
            );
            if (floor == null || floor.getBitmap() == null) {
                response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
                return;
            }
            IOUtils.copy(new ByteArrayInputStream(Base64.getEncoder().encode(floor.getBitmap())),
                    response.getOutputStream());
        } catch (NumberFormatException e) {
            response.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    private void authorize(HttpServletRequest request) {
        String authToken = request.getHeader("auth_token");
        if (authToken != null) {
            Person person = personRepository.findByAuthToken(authToken);
            if (person != null) {
                authorizationBean.setCurrentUser(person);
                return;
            }
        }
        throw new PermissionException();
    }

}
