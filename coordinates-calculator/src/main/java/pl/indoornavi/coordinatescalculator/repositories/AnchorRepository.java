package pl.indoornavi.coordinatescalculator.repositories;

import org.jooq.impl.DefaultDSLContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import pl.indoornavi.coordinatescalculator.models.AnchorDto;

import java.util.List;
import java.util.Optional;

import static org.jooq.codegen.maven.example.Tables.ANCHOR;
import static org.jooq.codegen.maven.example.Tables.UWB;

@Repository
public class AnchorRepository {
    @Autowired
    public AnchorRepository(
            DefaultDSLContext context,
            ModelMapper modelMapper) {
        this.context = context;
        this.modelMapper = modelMapper;
    }

    private final DefaultDSLContext context;
    private final ModelMapper modelMapper;

    @Cacheable("anchors")
    public List<AnchorDto> findByShortIdIn(List<Integer> connectedAnchors) {
        return context
                .select(UWB.SHORTID, ANCHOR.X, ANCHOR.Y, ANCHOR.Z)
                .from(ANCHOR)
                .join(UWB)
                .on(UWB.ID.eq(ANCHOR.ID))
                .where(UWB.SHORTID.in(connectedAnchors))
                .fetchInto(AnchorDto.class);
    }

    @Cacheable("floorId")
    public Optional<Long> findFloorIdByAnchorShortId(Integer anchorId) {
        return context
                .select(ANCHOR.FLOOR_ID)
                .from(ANCHOR)
                .join(UWB)
                .on(UWB.ID.eq(ANCHOR.ID))
                .where(UWB.SHORTID.eq(anchorId))
                .fetchOptional(ANCHOR.FLOOR_ID);
    }

    public List<AnchorDto> findAll() {
        return context
            .select(UWB.SHORTID, ANCHOR.X, ANCHOR.Y, ANCHOR.Z, ANCHOR.FLOOR_ID)
            .from(ANCHOR)
            .join(UWB)
            .on(UWB.ID.eq(ANCHOR.ID))
            .fetch(anchorRecord -> modelMapper.map(anchorRecord, AnchorDto.class));
    }

    @Cacheable("anchors")
    public Optional<AnchorDto> findByShortIdAndPositionNotNull(Integer anchorShortId) {
        return context
                .select(UWB.SHORTID, ANCHOR.X, ANCHOR.Y, ANCHOR.Z, ANCHOR.FLOOR_ID)
                .from(ANCHOR)
                .join(UWB)
                .on(UWB.ID.eq(ANCHOR.ID))
                .where(UWB.SHORTID.eq(anchorShortId).and(ANCHOR.X.isNotNull()).and(ANCHOR.Y.isNotNull()))
                .fetchOptional(
                        anchorRecord -> modelMapper.map(anchorRecord, AnchorDto.class)
                );
    }
}
