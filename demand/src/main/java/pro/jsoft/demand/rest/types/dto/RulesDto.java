package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;

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

public class RulesDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String profile;
    private boolean upload;
    private boolean confirm;
    private boolean accept;
    private boolean reaccept;
    private boolean approve;
}
