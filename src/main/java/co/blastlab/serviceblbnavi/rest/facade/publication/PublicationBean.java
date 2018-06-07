package co.blastlab.serviceblbnavi.rest.facade.publication;

import co.blastlab.serviceblbnavi.dao.repository.*;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.map.OriginChecker;
import co.blastlab.serviceblbnavi.dto.map.PublicationDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PublicationBean implements PublicationFacade {

	private final static Logger LOGGER = LoggerFactory.getLogger(PublicationBean.class);

	private final PublicationRepository publicationRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final FloorRepository floorRepository;
	private final ApiKeyRepository apiKeyRepository;

	@Context
	SecurityContext securityContext;

	@Inject
	public PublicationBean(PublicationRepository publicationRepository, TagRepository tagRepository,
	                       UserRepository userRepository, FloorRepository floorRepository, ApiKeyRepository apiKeyRepository) {
		this.publicationRepository = publicationRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
		this.floorRepository = floorRepository;
		this.apiKeyRepository = apiKeyRepository;
	}

	@Override
	public PublicationDto create(PublicationDto publication) {
		LOGGER.debug("Trying to create publication {}", publication);
		Publication publicationEntity = new Publication();
		return createOrUpdate(publicationEntity, publication);
	}

	@Override
	public PublicationDto update(Long id, PublicationDto publication) {
		LOGGER.debug("Trying to update publication {}", publication);
		Publication publicationEntity = publicationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(publicationEntity, publication);
	}

	@Override
	public List<PublicationDto> getAll() {
		return publicationRepository.findAll().stream().map(PublicationDto::new).collect(Collectors.toList());
	}

	@Override
	public Response delete(Long id) {
		LOGGER.debug("Trying to remove publication id {}", id);
		Publication map = publicationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		publicationRepository.remove(map);
		LOGGER.debug("Publication removed");
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	@Override
	public Boolean checkOrigin(OriginChecker originChecker) {
		LOGGER.debug("Checking origin {}", originChecker);
		Optional<ApiKey> optionalByValue = apiKeyRepository.findOptionalByValue(originChecker.getApiKey());
		return optionalByValue.isPresent() && optionalByValue.get().getHost().equals(originChecker.getOrigin());
	}

	@Override
	public List<TagDto> getTagsForUser(Long floorId) {
		LOGGER.debug("Getting tags for user, floor id {}", floorId);
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Publication> publications = publicationRepository.findAllContainingFloor(floor);
		LOGGER.debug("There is {} publications for floor", publications.size());
		User user = userRepository.findOptionalByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(EntityNotFoundException::new);
		publications = publications.stream().filter(publication -> publication.getUsers().contains(user)).collect(Collectors.toList());
		LOGGER.debug("There is {} publications for user {}", publications.size(), user);
		if (publications.size() == 0) {
			throw new ForbiddenException();
		}
		List<Tag> tags = new ArrayList<>();
		publications.forEach(publication -> tags.addAll(publication.getTags()));
		return tags.stream().map(TagDto::new).collect(Collectors.toList());
	}

	private PublicationDto createOrUpdate(Publication mapEntity, PublicationDto map) {
		List<Floor> floors = new ArrayList<>();
		map.getFloors().forEach(floorDto -> floors.add(floorRepository.findOptionalById(floorDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setFloors(floors);

		List<Tag> tags = new ArrayList<>();
		map.getTags().forEach(tagDto -> tags.add(tagRepository.findOptionalById(tagDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setTags(tags);

		List<User> users = new ArrayList<>();
		map.getUsers().forEach(userDto -> users.add(userRepository.findOptionalById(userDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setUsers(users);

		publicationRepository.save(mapEntity);
		LOGGER.debug("Publication created/updated");
		return new PublicationDto(mapEntity);
	}
}
