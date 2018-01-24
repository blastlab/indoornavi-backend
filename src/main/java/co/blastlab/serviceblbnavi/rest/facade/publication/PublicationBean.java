package co.blastlab.serviceblbnavi.rest.facade.publication;

import co.blastlab.serviceblbnavi.dao.repository.*;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.map.OriginChecker;
import co.blastlab.serviceblbnavi.dto.map.PublicationDto;
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
public class PublicationBean implements PublicationFacade {

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
		Publication publicationEntity = new Publication();
		return createOrUpdate(publicationEntity, publication);
	}

	@Override
	public PublicationDto update(Long id, PublicationDto publication) {
		Publication publicationEntity = publicationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(publicationEntity, publication);
	}

	@Override
	public List<PublicationDto> getAll() {
		return publicationRepository.findAll().stream().map(PublicationDto::new).collect(Collectors.toList());
	}

	@Override
	public PublicationDto get(Long floorId) {
		Floor floor = floorRepository.findOptionalById(floorId).orElseThrow(EntityNotFoundException::new);
		List<Publication> publications = publicationRepository.findByFloor(floor);
		User user = userRepository.findOptionalByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(EntityNotFoundException::new);
		PublicationDto publication = null;
		for (Publication publicationEntity : publications) {
			if (publicationEntity.getUsers().contains(user)) {
				publication = new PublicationDto(publicationEntity);
				break;
			}
		}
		if (publication == null) {
			throw new ForbiddenException();
		}
		return publication;
	}

	@Override
	public Response delete(Long id) {
		Publication map = publicationRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		publicationRepository.remove(map);
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	@Override
	public Boolean checkOrigin(OriginChecker originChecker) {
		Optional<ApiKey> optionalByValue = apiKeyRepository.findOptionalByValue(originChecker.getApiKey());
		return optionalByValue.isPresent() && optionalByValue.get().getHost().equals(originChecker.getOrigin());
	}

	private PublicationDto createOrUpdate(Publication mapEntity, PublicationDto map) {
		Floor floor = floorRepository.findOptionalById(map.getFloor().getId()).orElseThrow(EntityNotFoundException::new);
		mapEntity.setFloor(floor);

		List<Tag> tags = new ArrayList<>();
		map.getTags().forEach(tagDto -> tags.add(tagRepository.findOptionalById(tagDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setTags(tags);

		List<User> users = new ArrayList<>();
		map.getUsers().forEach(userDto -> users.add(userRepository.findOptionalById(userDto.getId()).orElseThrow(EntityNotFoundException::new)));
		mapEntity.setUsers(users);

		publicationRepository.save(mapEntity);
		return new PublicationDto(mapEntity);
	}
}
