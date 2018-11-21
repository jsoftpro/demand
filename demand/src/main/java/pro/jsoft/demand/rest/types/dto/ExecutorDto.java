package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ExecutorDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;	
    private String name;
    private String uid;
    private String email;
    private String phone;
    private Long organizationId;
    private String organizationName;
    private Long departmentId;
    private String departmentName;
    private Long servicedOrganizationId;
    private String servicedOrganizationName;
    private Long servicedDepartmentId;
    private String servicedDepartmentName;
    private String branch;
}
