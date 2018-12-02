package pro.jsoft.demand.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Demand;

@Service
@Slf4j
public class NotificatorService {
	public void notificate(Demand demand) {
		log.debug("notification");
	}
}
