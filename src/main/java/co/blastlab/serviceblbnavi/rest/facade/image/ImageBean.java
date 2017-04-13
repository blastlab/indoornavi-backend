package co.blastlab.serviceblbnavi.rest.facade.image;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.ImageRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Image;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.ext.mapper.content.FileViolationContent;
import co.blastlab.serviceblbnavi.properties.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static co.blastlab.serviceblbnavi.ext.mapper.accessory.FileMessagePack.FILE_002;

@Stateless
public class ImageBean implements ImageFacade {

	@Inject
	private ImageRepository imageRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	@ConfigProperty(name = "max.file.size")
	private Integer maxFileSize;

	@Inject
	@ConfigProperty(name = "allowed.types")
	private String allowedTypes;

	@Override
	public Response uploadImage(Long floorId, @MultipartForm ImageUpload imageUpload) throws IOException {
		Optional<Floor> floorOptional = floorRepository.findById(floorId);
		if (floorOptional.isPresent()) {
			Image imageEntity = imageRepository.findBy(floorId);
			if (imageEntity == null) {
				Floor floorEntity = floorOptional.get();
				imageEntity = new Image();
				byte[] image = imageUpload.getImage();

				if (!isProperSize(image)) {
					return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new FileViolationContent(FILE_002)).build();
				}

				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
				imageEntity.setBitmapHeight(bufferedImage.getHeight());
				imageEntity.setBitmapWidth(bufferedImage.getWidth());
				imageEntity.setBitmap(image);
				imageRepository.save(imageEntity);
				floorEntity.setImage(imageEntity);
				return Response.ok(new FloorDto(floorEntity)).build();
			}
			return Response.status(HttpStatus.SC_CONFLICT).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response downloadImage(Long id) {
		Optional<Image> imageOptional = imageRepository.findById(id);
		if (imageOptional.isPresent()) {
			byte[] image = imageOptional.get().getBitmap();

			StreamingOutput stream = outputStream -> {
				try {
					outputStream.write(IOUtils.toByteArray(new ByteArrayInputStream(image)));
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			};

			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Properties retrievePropertiesOfImages() {
		return new Properties(this.maxFileSize, this.allowedTypes.split(";"));
	}

	private boolean isProperSize(byte[] image){
		return image.length <= this.maxFileSize;
	}
}
