package pl.indoornavi.coordinatescalculator.repositories;

import org.jooq.impl.DefaultDSLContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.codegen.maven.example.Tables.TAG;
import static org.jooq.codegen.maven.example.Tables.UWB;


@Repository
public class TagRepository {
    public TagRepository(DefaultDSLContext dslContext) {
        this.dslContext = dslContext;
    }
    private final DefaultDSLContext dslContext;

    @Cacheable("tagId")
    public Optional<Long> getTagIdByShortId(Integer shortId) {
        return dslContext
                .select(TAG.ID)
                .from(TAG)
                .innerJoin(UWB)
                .on(TAG.ID.eq(UWB.ID))
                .where(UWB.SHORTID.eq(shortId))
                .fetchOptional(TAG.ID);
    }
}
