package co.blastlab.serviceblbnavi.rest.facade.image;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.ImageRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Image;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.ext.mapper.content.FileViolationContent;
import co.blastlab.serviceblbnavi.properties.Properties;
import net.sf.jmimemagic.*;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static co.blastlab.serviceblbnavi.ext.mapper.accessory.FileMessagePack.FILE_001;
import static co.blastlab.serviceblbnavi.ext.mapper.accessory.FileMessagePack.FILE_002;

@Stateless
public class ImageBean implements ImageFacade {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImageBean.class);

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
	public Response uploadImage(Long floorId, ImageUpload imageUpload) throws IOException {
		LOGGER.debug("Trying to upload image to floor id {}", floorId);
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		if (floor.getImage() == null) {
			Image imageEntity = new Image();
			byte[] image = imageUpload.getImage();

			LOGGER.debug("Checking file size");
			if (!isProperFileSize(image)) {
				return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new FileViolationContent(FILE_002)).build();
			}

			LOGGER.debug("Checking file extension");
			if (!isProperFileExtension(image)) {
				return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new FileViolationContent(FILE_001)).build();
			}

			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
			imageEntity.setBitmapHeight(bufferedImage.getHeight());
			imageEntity.setBitmapWidth(bufferedImage.getWidth());
			imageEntity.setBitmap(image);
			imageRepository.save(imageEntity);
			floor.setImage(imageEntity);
			LOGGER.debug("Image uploaded");
			return Response.ok(new FloorDto(floor)).build();
		}
		LOGGER.debug("Floor id {} has already an image", floorId);
		return Response.status(HttpStatus.SC_CONFLICT).build();
	}

	@Override
	public Response downloadImage(Long id) {
		LOGGER.debug("Trying to download image id {}", id);
		Optional<Image> imageOptional = imageRepository.findOptionalById(id);
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

	private boolean isProperFileSize(byte[] image){
		return image.length <= this.maxFileSize;
	}

	private boolean isProperFileExtension(byte[] image) {
		try {
			MagicMatch match = Magic.getMagicMatch(image);
			String mimeType = match.getMimeType();
			return mimeType.equals("image/jpeg") || mimeType.equals("image/png");
		} catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
			return false;
		}
	}
}
