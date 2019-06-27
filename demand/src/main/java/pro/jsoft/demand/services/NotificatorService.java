package pro.jsoft.demand.services;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Demand;

@Service
@Slf4j
public class NotificatorService {

	private final JavaMailSender javaMailSender;

	@Autowired
	public NotificatorService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void notificate(Demand demand) {
		log.debug("notification");
		// TODO:
	}
	
	@Async
	public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] from '{}' to '{}' with subject '{}' and content={}", 
				isMultipart, isHtml, from, to, subject, content);

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setFrom(from);
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.warn("Email could not be sent to user '{}'", to, e);
			} else {
				log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
			}
		}
	}
}
