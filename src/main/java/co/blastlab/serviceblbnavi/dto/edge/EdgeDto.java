package co.blastlab.serviceblbnavi.dto.edge;

import co.blastlab.serviceblbnavi.domain.Edge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class EdgeDto {

    public EdgeDto(Edge edge) {
        this.setId(edge.getId());
        this.setWeight(edge.getWeight());
        this.setSourceId(edge.getSource() != null ? edge.getSource().getId() : null);
        this.setTargetId(edge.getTarget() != null ? edge.getTarget().getId() : null);
    }

    private Long id;

    @NotNull
    @Min(0)
    private Double weight;

    @NotNull
    private Long sourceId;

    @NotNull
    private Long targetId;
}