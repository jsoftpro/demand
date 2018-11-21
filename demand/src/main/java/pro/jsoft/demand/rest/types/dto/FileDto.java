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

public class FileDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String contentType = "application/octet-stream";
	private String name;
	private Long size;
	private String description;
}
