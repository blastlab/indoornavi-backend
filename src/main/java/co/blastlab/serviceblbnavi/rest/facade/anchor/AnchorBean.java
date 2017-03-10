package co.blastlab.serviceblbnavi.rest.facade.anchor;

import co.blastlab.serviceblbnavi.domain.Anchor;

import javax.ejb.Stateless;

@Stateless
public class AnchorBean implements AnchorFacade {

	public Anchor create(Anchor anchor) {
		return new Anchor();
	}
}
