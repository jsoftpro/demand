package pro.jsoft.demand.rest.types;

import java.io.Serializable;
import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DateRange implements Serializable {
	private static final long serialVersionUID = 1L;

	private Calendar from;
	private Calendar to;
}
