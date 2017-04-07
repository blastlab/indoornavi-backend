package co.blastlab.serviceblbnavi.rest.facade.tag;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.rest.facade.device.DeviceBean;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class TagBean extends DeviceBean implements TagFacade {

	@Inject
	private TagRepository tagRepository;

	@Inject
	private FloorRepository floorRepository;

	@Override
	public TagDto create(TagDto tag) {
		Tag tagEntity = new Tag();
		tagEntity.setShortId(tag.getShortId());
		tagEntity.setLongId(tag.getLongId());
		tagEntity.setName(tag.getName());
		tagEntity.setVerified(tag.getVerified());

		if (tag.getFloorId() != null) {
			super.setFloor(tag, tagEntity);
		}
		tagEntity = tagRepository.save(tagEntity);
		return new TagDto(tagEntity);
	}

	@Override
	public TagDto update(Long id, TagDto tag) {
		Optional<Tag> tagOptional = tagRepository.findById(id);
		if (tagOptional.isPresent()){
			Tag tagEntity = tagOptional.get();
			tagEntity.setShortId(tag.getShortId());
			tagEntity.setLongId(tag.getLongId());
			tagEntity.setName(tag.getName());
			tagEntity.setVerified(tag.getVerified());

			if (tag.getFloorId() != null) {
				super.setFloor(tag, tagEntity);
			} else {
				tagEntity.setFloor(null);
			}
			tagRepository.save(tagEntity);
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
		Optional<Tag> tag = tagRepository.findById(id);
		if (tag.isPresent()) {
			tagRepository.remove(tag.get());
			return Response.status(HttpStatus.SC_NO_CONTENT).build();
		}
		throw new EntityNotFoundException();
	}
}
