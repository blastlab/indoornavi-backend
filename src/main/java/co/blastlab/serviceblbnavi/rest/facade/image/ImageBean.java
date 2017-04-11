package co.blastlab.serviceblbnavi.rest.facade.image;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.ImageRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Image;
import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.ext.mapper.content.FileViolationContent;
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

import static co.blastlab.serviceblbnavi.ext.mapper.accessory.FileMessagePack.FILE_002;
import static co.blastlab.serviceblbnavi.rest.RestApplication.MAX_FILE_SIZE_LIMIT_IN_BYTES;

@Stateless
public class ImageBean implements ImageFacade {

	@Inject
	private ImageRepository imageRepository;

	@Inject
	private FloorRepository floorRepository;

	@Override
	public Response uploadImage(Long floorId , @MultipartForm ImageUpload imageUpload) throws IOException {
		Optional<Floor> floorOptional = floorRepository.findById(floorId);
		if (floorOptional.isPresent()){
			Image imageEntity = imageRepository.findBy(floorId);
			if (imageEntity == null){
				imageEntity = new Image();
				byte[] image = imageUpload.getImage();

				if (image.length > MAX_FILE_SIZE_LIMIT_IN_BYTES){
					return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new FileViolationContent(FILE_002)).build();
				}

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
