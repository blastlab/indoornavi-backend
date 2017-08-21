package co.blastlab.serviceblbnavi.rest.facade.map;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.MapRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Map;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.dto.map.MapDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class MapBean implements MapFacade {

	private final MapRepository mapRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final FloorRepository floorRepository;

	@Inject
	public MapBean(MapRepository mapRepository, TagRepository tagRepository,
	               UserRepository userRepository, FloorRepository floorRepository) {
		this.mapRepository = mapRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
		this.floorRepository = floorRepository;
	}

	@Override
	public MapDto create(MapDto map) {
		Map mapEntity = new Map();
		return createOrUpdate(mapEntity, map);
	}

	@Override
	public MapDto update(Long id, MapDto map) {
		Map mapEntity = mapRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(mapEntity, map);
	}

	@Override
	public List<MapDto> getAll() {
		return mapRepository.findAll().stream().map(MapDto::new).collect(Collectors.toList());
	}

	@Override
	public MapDto get(Long id) {
		return new MapDto(mapRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new));
	}

	@Override
	public Response delete(Long id) {
		Map map = mapRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		mapRepository.remove(map);
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	private MapDto createOrUpdate(Map mapEntity, MapDto map) {
		Floor floor = floorRepository.findOptionalById(map.getFloorId()).orElseThrow(EntityNotFoundException::new);
		mapEntity.setFloor(floor);

		List<Tag> tags = new ArrayList<>();
		map.getTags().forEach(tagId -> tags.add(tagRepository.findOptionalById(tagId).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setTags(tags);

		List<User> users = new ArrayList<>();
		map.getUsers().forEach(userId -> users.add(userRepository.findOptionalById(userId).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setUsers(users);

		mapRepository.save(mapEntity);
		return new MapDto(mapEntity);
	}
}
