package co.blastlab.serviceblbnavi.rest.facade.ext;

import co.blastlab.serviceblbnavi.domain.Floor;

public interface Updatable {

    Long getFloorId();

    Long getId();

    Floor getFloor();

    void setFloor(Floor floor);

    void setX(Double x);

    void setY(Double y);

    Double getX();

    Double getY();

    void setInactive(Boolean bool);
}
