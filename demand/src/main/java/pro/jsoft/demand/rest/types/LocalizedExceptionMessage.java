package pro.jsoft.demand.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocalizedExceptionMessage {

	private final Throwable throwable;

	public LocalizedExceptionMessage(Throwable throwable) {
		this.throwable = throwable;
	}

	@JsonProperty("message")
	public String getMessage() {
		if (throwable == null) return null;
		return throwable.getMessage();
	}

	@JsonProperty("localized")
	public String getLocalizedMessage() {
		if (throwable == null) return null;
		return throwable.getLocalizedMessage();
	}

	@JsonProperty("cause")
	public LocalizedExceptionMessage getCause() {
		if (throwable == null) return null;
		return throwable.getCause() != null ? new LocalizedExceptionMessage(throwable.getCause()) : null;
	}
}