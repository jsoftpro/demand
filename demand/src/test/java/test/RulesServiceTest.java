package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.services.RulesService;
import test.user.MockUserServiceConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MockUserServiceConfig.class, MockRepositoriesConfig.class })
@ActiveProfiles({"test"})
public class RulesServiceTest {
	@Autowired
	private RulesService rulesService;
	
	@Test
	public void testRules() {
		List<String> actions = rulesService.getActions("TEST2");
		assertFalse(actions.contains(Action.DO_UPLOAD.name()));
		assertTrue(actions.contains(Action.STEP_ACCEPT.name()));
	}
}
