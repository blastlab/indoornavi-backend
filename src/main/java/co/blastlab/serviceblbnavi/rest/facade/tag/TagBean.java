package co.blastlab.serviceblbnavi.rest.facade.tag;

import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class TagBean implements TagFacade {

	@Inject
	private Logger logger;

	@Inject
	private TagRepository tagRepository;

	@Override
	public TagDto create(TagDto tag) {
		logger.debug("Trying to create tag {}", tag);
		Tag tagEntity = new Tag();
		tagEntity.setShortId(tag.getShortId());
		tagEntity.setMac(tag.getMacAddress());
		tagEntity.setName(tag.getName());
		tagEntity.setVerified(tag.getVerified());

		tagEntity = tagRepository.save(tagEntity);
		logger.debug("Tag created");
		return new TagDto(tagEntity);
	}

	@Override
	public TagDto update(Long id, TagDto tag) {
		logger.debug("Trying to update tag {}", tag);
		Optional<Tag> tagOptional = tagRepository.findOptionalById(id);
		if (tagOptional.isPresent()){
			Tag tagEntity = tagOptional.get();
			tagEntity.setShortId(tag.getShortId());
			tagEntity.setMac(tag.getMacAddress());
			tagEntity.setName(tag.getName());
			tagEntity.setVerified(tag.getVerified());

			tagRepository.save(tagEntity);
			logger.debug("Tag updated");
			return new TagDto(tagEntity);
		}
		throw new EntityNotFoundException();
	}

	@Override
	public List<TagDto> findAll() {
		List<TagDto> tags = new ArrayList<>();
		tagRepository.findAll()
			.forEach(tagEntity -> tags.add(new TagDto(tagEntity)));
		return tags;
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove tag id = {}", id);
		Optional<Tag> tag = tagRepository.findOptionalById(id);
		if (tag.isPresent()) {
			tagRepository.remove(tag.get());
			logger.debug("Tag removed");
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}
}
