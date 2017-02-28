package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.BuildingRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Stateless
public class FloorBean implements FloorFacade {

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private BuildingRepository buildingRepository;

	@Inject
	private PermissionBean permissionBean;

	public FloorDto create(FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if (building != null) {
			Floor floorEntity = new Floor();
			return createOrUpdate(floorEntity, floor, building);
		}
		throw new EntityNotFoundException();
	}

	public FloorDto find(Long id) {
		Floor floor = floorRepository.findBy(id);
		if (floor != null) {
			permissionBean.checkPermission(floor, Permission.READ);
			return new FloorDto(floor);
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long id) {
		Floor floor = floorRepository.findBy(id);
		if (floor != null) {
			permissionBean.checkPermission(floor, Permission.UPDATE);
			floorRepository.remove(floor);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public FloorDto update(FloorDto floor) {
		Building building = buildingRepository.findBy(floor.getBuildingId());
		if (building != null) {
			Floor floorEntity = floorRepository.findBy(floor.getId());
			return createOrUpdate(floorEntity, floor, building);
		}
		throw new EntityNotFoundException();
	}

	public Response updateFloors(Long buildingId, List<FloorDto> floors) {
		Building building = buildingRepository.findBy(buildingId);
		if (building != null) {
			permissionBean.checkPermission(building, Permission.UPDATE);
			List<Floor> floorEntities = new ArrayList<>();
			floors.forEach((floor -> {
				Floor floorEntity = floorRepository.findBy(floor.getId());
				if (floorEntity == null) {
					throw new EntityNotFoundException();
				}
				floorEntities.add(floorEntity);
			}
			));

			floorEntities.forEach((floorEntity -> floorEntity.setBuilding(building)));
			updateFloorLevels(floorEntities);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public Response updatemToPix(FloorDto.Extended floor) {
		Floor floorEntity = floorRepository.findBy(floor.getId());
		if (floorEntity != null) {
			permissionBean.checkPermission(floorEntity, Permission.UPDATE);
			floorEntity.setMToPix(floor.getMToPix());
			floorRepository.save(floorEntity);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public Response uploadImage(@MultipartForm ImageUpload imageUpload) throws IOException {
		Floor floorEntity = floorRepository.findBy(imageUpload.getFloorId());
		if (floorEntity == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		byte[] image = imageUpload.getImage();
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
		floorEntity.setBitmapHeight(bufferedImage.getHeight());
		floorEntity.setBitmapWidth(bufferedImage.getWidth());
		floorEntity.setBitmap(image);
		return Response.ok().build();
	}

	public Response downloadImage(Long floorId) {
		Floor floorEntity = floorRepository.findBy(floorId);
		if (floorEntity == null || floorEntity.getBitmap() == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(new ByteArrayInputStream(Base64.getEncoder().encode(floorEntity.getBitmap()))).build();
	}

	private void updateFloorLevels(List<Floor> floors) {
		floors.stream().map((f) -> {
			Floor floor = floorRepository.findBy(f.getId());
			floor.setLevel(f.getLevel());
			return floor;
		}).forEach((floor) -> floorRepository.save(floor));
	}

	private FloorDto createOrUpdate(Floor floorEntity, FloorDto floor, Building building) {
		permissionBean.checkPermission(building, Permission.UPDATE);
		floorEntity.setLevel(floor.getLevel());
		floorEntity.setBuilding(building);
		floorEntity = floorRepository.save(floorEntity);
		return new FloorDto(floorEntity);
	}
}