package co.blastlab.serviceblbnavi.rest.facade.ext;

import co.blastlab.serviceblbnavi.domain.Floor;

public interface UpdatableEntity {

	void setX(Double x);

	void setY(Double y);

	Floor getFloor();

	void setInactive(boolean inactive);
}
