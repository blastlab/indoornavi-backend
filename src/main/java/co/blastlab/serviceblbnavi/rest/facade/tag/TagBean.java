package co.blastlab.serviceblbnavi.rest.facade.tag;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TagBean implements TagFacade {

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
		if (tag.getFloorId() != null) {
			Floor floor = floorRepository.findBy(tag.getFloorId());
			if (floor != null) {
				tagEntity.setFloor(floor);
			} else {
				throw new EntityNotFoundException();
			}
		}

		tagEntity = tagRepository.save(tagEntity);
		return new TagDto(tagEntity);
	}

	@Override
	public TagDto update(Long id, TagDto tag) {
		Tag tagEntity = tagRepository.findBy(id);
		if (tagEntity != null){
			tagEntity.setName(tag.getName());
			if (tag.getFloorId() != null) {
				Floor floor = floorRepository.findBy(tag.getFloorId());
				if (floor != null) {
					tagEntity.setFloor(floor);
				} else {
					throw new EntityNotFoundException();
				}
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
		if (id != null) {
			Tag tag = tagRepository.findBy(id);
			if (tag != null) {
				tagRepository.remove(tag);
				return Response.status(HttpStatus.SC_NO_CONTENT).build();
			}
		}
		throw new EntityNotFoundException();
	}
}
