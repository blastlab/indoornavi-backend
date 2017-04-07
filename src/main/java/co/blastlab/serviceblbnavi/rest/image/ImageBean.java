package co.blastlab.serviceblbnavi.rest.image;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.ImageRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Image;
import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;


@Stateless
public class ImageBean implements ImageFacade {

	@Inject
	private ImageRepository imageRepository;

	@Inject
	private FloorRepository floorRepository;

	@Override
	public Response uploadImage(@MultipartForm ImageUpload imageUpload) throws IOException {
		Optional<Floor> floorOptional = floorRepository.findById(imageUpload.getFloorId());
		if (floorOptional.isPresent()){
			Image imageEntity = imageRepository.findBy(imageUpload.getFloorId());
			if (imageEntity == null){
				byte[] image = imageUpload.getImage();
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
				imageEntity.setBitmapHeight(bufferedImage.getHeight());
				imageEntity.setBitmapWidth(bufferedImage.getWidth());
				imageEntity.setBitmap(image);
				return Response.ok().build();
			}
			return Response.status(HttpStatus.SC_CONFLICT).build();  //istnieje obrazek o takim floorId (409)
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response downloadImage(Long id) {
		Optional<Image> imageOptional = imageRepository.findById(id);
		if (imageOptional.isPresent()){
			return Response.ok(new ByteArrayInputStream(Base64.getEncoder().encode(imageOptional.get().getBitmap()))).build();
		}
		throw new EntityNotFoundException();
	}
}
