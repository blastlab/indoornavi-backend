package pl.indoornavi.coordinatescalculator.repositories;

import org.jooq.InsertValuesStepN;
import org.jooq.codegen.maven.example.tables.records.UwbcoordinatesRecord;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.indoornavi.coordinatescalculator.services.CoordinatesService;

import java.io.IOException;

import static org.jooq.codegen.maven.example.Tables.UWBCOORDINATES;

@Repository
public class CoordinatesRepository {
    @Autowired
    public CoordinatesRepository(DefaultDSLContext dslContext, CoordinatesService coordinatesService) {
        this.dslContext = dslContext;
        this.coordinatesService = coordinatesService;
    }

    private final DefaultDSLContext dslContext;
    private final CoordinatesService coordinatesService;

    public void saveStoredCoordinates() throws IOException {
        if (coordinatesService.getCoordinatesToSave().isEmpty()) {
            return;
        }

        InsertValuesStepN<UwbcoordinatesRecord> step = dslContext
                .insertInto(UWBCOORDINATES, UWBCOORDINATES.fields());
        coordinatesService.getCoordinatesToSave().forEach(coordinatesRecord -> {
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

        coordinatesService.clearCoordinatesToSave();
    }
}
