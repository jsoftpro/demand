package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

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

public class ValueDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	@NotBlank(message = "BLANK_VALUE")
	private String text;
}
