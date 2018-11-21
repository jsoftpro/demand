package pro.jsoft.demand.persistence.model;

import java.io.IOException;
import java.io.UncheckedIOException;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyGroup;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@Entity
@Table(name = "DEM_FILE")
public class FileComplete extends FileInfo {
	private static final long serialVersionUID = 1L;
	
	public FileComplete(MultipartFile file) {
		setName(file.getOriginalFilename());
		setContentType(file.getContentType());
		setSize(file.getSize());
		try {
			setContent(file.getBytes());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
	@Lob
	@Column(nullable = false, updatable = false)
	@Basic(fetch = FetchType.LAZY, optional = false)
	@LazyGroup("lobs")
	private byte[] content;
}
