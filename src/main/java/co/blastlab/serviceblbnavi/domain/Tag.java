package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@Cacheable
public class Tag extends Device {

}