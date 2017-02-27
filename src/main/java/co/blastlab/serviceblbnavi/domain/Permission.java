package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class Permission extends CustomIdGenerationEntity implements Serializable {

	public static final String READ = "READ";
	public static final String CREATE = "CREATE";
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";

	@Column(unique = true)
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "permission")
	private List<ACL_Complex> aclComplexes;
}
