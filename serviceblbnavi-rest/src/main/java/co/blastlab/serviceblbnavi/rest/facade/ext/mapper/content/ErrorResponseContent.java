package co.blastlab.serviceblbnavi.rest.facade.ext.mapper.content;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Maciej Radzikowski
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class ErrorResponseContent {

	@XmlElement
	public abstract String getError();

}
