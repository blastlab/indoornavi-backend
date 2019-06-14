package pl.indoornavi.coordinatescalculator.services;

import lombok.Getter;
import org.jooq.codegen.maven.example.tables.records.UwbcoordinatesRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.indoornavi.coordinatescalculator.models.UwbCoordinates;
import pl.indoornavi.coordinatescalculator.repositories.TagRepository;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class CoordinatesService {
    private static Logger logger = LoggerFactory.getLogger(CoordinatesService.class);

    public CoordinatesService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    private final TagRepository tagRepository;

    @Getter
    private Queue<UwbcoordinatesRecord> coordinatesToSave = new ConcurrentLinkedQueue<>();
    private Map<Integer, UwbCoordinates> coordinatesToSend = new ConcurrentHashMap<>();

    public List<UwbCoordinates> getCoordinatesToSend() {
        List<UwbCoordinates> values = new ArrayList<>(this.coordinatesToSend.values());
        coordinatesToSend.clear();
        return values;
    }

    public void add(UwbCoordinates coordinatesDto) {
        Optional<Long> optionalTagId = tagRepository.getTagIdByShortId(coordinatesDto.getTagId());
        if (optionalTagId.isPresent()) {
            coordinatesToSave.add(
                    new UwbcoordinatesRecord(
                            null,
                            optionalTagId.get(),
                            new Timestamp(new Date().getTime()),
                            coordinatesDto.getFloorId(),
                            new Timestamp(coordinatesDto.getTime().getTime()),
                            null,
                            coordinatesDto.getX(),
                            coordinatesDto.getY(),
                            coordinatesDto.getZ()
                    )
            );
        } else {
            logger.trace("The tag with short id: {} could not be found in database", coordinatesDto.getTagId());
        }

        coordinatesToSend.put(coordinatesDto.getTagId(), coordinatesDto);
    }

    public void clearCoordinatesToSave() { coordinatesToSave.clear(); }
}
