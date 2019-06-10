package pl.indoornavi.coordinatescalculator.repositories;

import lombok.Getter;
import org.jooq.InsertValuesStepN;
import org.jooq.codegen.maven.example.tables.records.UwbcoordinatesRecord;
import org.jooq.impl.DefaultDSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.indoornavi.coordinatescalculator.models.UwbCoordinatesDto;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.jooq.codegen.maven.example.Tables.*;

@Repository
public class CoordinatesRepository {
    private static Logger logger = LoggerFactory.getLogger(CoordinatesRepository.class);

    @Autowired
    public CoordinatesRepository(DefaultDSLContext dslContext, TagRepository tagRepository) {
        this.dslContext = dslContext;
        this.tagRepository = tagRepository;
    }

    private final DefaultDSLContext dslContext;

    private final TagRepository tagRepository;

    private Queue<UwbcoordinatesRecord> coordinatesToSave = new ConcurrentLinkedQueue<>();

    @Getter
    private Map<Integer, UwbCoordinatesDto> coordinatesToSend = new ConcurrentHashMap<>();

    public void addToSave(UwbCoordinatesDto coordinatesDto) {
        Optional<Long> optionalTagId = tagRepository.getTagIdByShortId(coordinatesDto.getTagShortId());
        if (optionalTagId.isPresent()) {
            coordinatesToSave.add(
                    new UwbcoordinatesRecord(
                            null,
                            optionalTagId.get(),
                            new Timestamp(new Date().getTime()),
                            coordinatesDto.getFloorId(),
                            new Timestamp(coordinatesDto.getMeasurementTime().getTime()),
                            null,
                            (int)Math.round(coordinatesDto.getPoint().getX()),
                            (int)Math.round(coordinatesDto.getPoint().getY()),
                            (int)Math.round(coordinatesDto.getPoint().getZ())
                    )
            );
            coordinatesToSend.put(coordinatesDto.getTagShortId(), coordinatesDto);
        } else {
            logger.trace("The tag with short id: {} could not be found in database", coordinatesDto.getTagShortId());
        }
    }

    public void saveStoredCoordinates() throws IOException {
        if (coordinatesToSave.isEmpty()) {
            return;
        }

        InsertValuesStepN<UwbcoordinatesRecord> step = dslContext
                .insertInto(UWBCOORDINATES, UWBCOORDINATES.fields());
        coordinatesToSave.forEach(coordinatesRecord -> {
            step.values(
                    coordinatesRecord.getId(),
                    coordinatesRecord.getTagId(),
                    coordinatesRecord.getCreationdate(),
                    coordinatesRecord.getFloorId(),
                    coordinatesRecord.getMeasurementtime(),
                    coordinatesRecord.getModificationdate(),
                    coordinatesRecord.getX(),
                    coordinatesRecord.getY(),
                    coordinatesRecord.getZ()
            );
        });
        step.execute();

        coordinatesToSave.clear();
    }

    public void clearCoordinatesToSend() {
        coordinatesToSend.clear();
    }
}
