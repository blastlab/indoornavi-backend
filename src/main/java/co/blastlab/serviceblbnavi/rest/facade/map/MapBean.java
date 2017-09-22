package co.blastlab.serviceblbnavi.rest.facade.map;

import co.blastlab.serviceblbnavi.dao.repository.*;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.map.MapDto;
import co.blastlab.serviceblbnavi.dto.map.OriginChecker;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class MapBean implements MapFacade {

	private final MapRepository mapRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final FloorRepository floorRepository;
	private final ApiKeyRepository apiKeyRepository;

	@Context
	SecurityContext securityContext;

	@Inject
	public MapBean(MapRepository mapRepository, TagRepository tagRepository,
	               UserRepository userRepository, FloorRepository floorRepository, ApiKeyRepository apiKeyRepository) {
		this.mapRepository = mapRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
		this.floorRepository = floorRepository;
		this.apiKeyRepository = apiKeyRepository;
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
		Map map = mapRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		User user = userRepository.findOptionalByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(EntityNotFoundException::new);
		if (map.getUsers().contains(user)) {
			return new MapDto(map);
		}
		throw new ForbiddenException();
	}

	@Override
	public Response delete(Long id) {
		Map map = mapRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		mapRepository.remove(map);
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	@Override
	public Boolean checkOrigin(OriginChecker originChecker) {
		Optional<ApiKey> optionalByValue = apiKeyRepository.findOptionalByValue(originChecker.getApiKey());
		return optionalByValue.isPresent() && optionalByValue.get().getHost().equals(originChecker.getOrigin());
	}

	private MapDto createOrUpdate(Map mapEntity, MapDto map) {
		Floor floor = floorRepository.findOptionalById(map.getFloor().getId()).orElseThrow(EntityNotFoundException::new);
		mapEntity.setFloor(floor);

		List<Tag> tags = new ArrayList<>();
		map.getTags().forEach(tagDto -> tags.add(tagRepository.findOptionalById(tagDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setTags(tags);

		List<User> users = new ArrayList<>();
		map.getUsers().forEach(userDto -> users.add(userRepository.findOptionalById(userDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setUsers(users);

		mapRepository.save(mapEntity);
		return new MapDto(mapEntity);
	}
}
