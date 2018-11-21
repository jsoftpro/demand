package pro.jsoft.demand.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */


@Entity
@Table(name = "DEM_FILE")
public class File extends FileInfo {
	private static final long serialVersionUID = 1L;
}
