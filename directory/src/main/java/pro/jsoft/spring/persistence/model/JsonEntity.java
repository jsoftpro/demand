package pro.jsoft.spring.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class JsonEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAXLENGTH = 1333;

	@Column(length = MAXLENGTH)
	private String json1;

	@Column(length = MAXLENGTH)
	private String json2;

	public void setJson(String json) {
		int length = json.length();
		if (length <= MAXLENGTH) {
			this.json1 = json;
			this.json2 = null;
		} else {
			this.json1 = json.substring(0, MAXLENGTH);
			this.json2 = json.substring(MAXLENGTH);
		}
	}

	public String getJson() {
		if (json2 != null) {
			return new StringBuilder(json1).append(json2).toString();
		}
		return json1;
	}
}
