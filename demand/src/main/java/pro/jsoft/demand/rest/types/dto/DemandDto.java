package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DemandDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
    @NotEmpty(message = "EMPTY_PROFILE")
	private String profile;
    private Long staffId;
    private String name;
    private String uid;
    private String email;
    private String phone;
    private String code;
    private String position;
    private String location;
    private Long organizationId;
    private String organizationName;
    private Long departmentId;
    private String departmentName;
    private String branch;
    @NotEmpty(message = "EMPTY_FORMNAME")
	private String formName;
    private Integer rate;
    private String opinion;
	private LinkedHashSet<ValueDto> values = new LinkedHashSet<>();
	private LinkedHashSet<FileDto> files = new LinkedHashSet<>();
    private LinkedHashSet<StageDto> stages = new LinkedHashSet<>();
    private List<String> actions = new ArrayList<>();
    
    public StageDto getCreated() {
		return stages.iterator().next();
    }
    
    public StageDto getChanged() {
		val stagesIterator = stages.iterator();
		StageDto changed = null;
		while (stagesIterator.hasNext()) {
			changed = stagesIterator.next();
		}
		return changed;
    }
}
