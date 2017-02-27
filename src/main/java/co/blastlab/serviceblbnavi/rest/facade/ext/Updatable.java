package co.blastlab.serviceblbnavi.rest.facade.ext;

public interface Updatable<T, S> {

	Long getId();

	Long getFloorId();

	void setFloorId(Long floorId);

	void setX(Double x);

	void setY(Double y);

	Double getX();

	Double getY();

	void setInactive(boolean bool);

	T create(S entity);
}
