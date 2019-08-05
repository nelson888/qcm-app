package com.polytech.qcm.server.qcmserver;

import com.google.gson.Gson;
import com.polytech.qcm.server.qcmserver.controller.AuthController;
import com.polytech.qcm.server.qcmserver.controller.QcmController;
import com.polytech.qcm.server.qcmserver.controller.ResponseController;
import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.ChoiceIds;
import com.polytech.qcm.server.qcmserver.data.PrincipalUser;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.data.response.AuthResponse;
import com.polytech.qcm.server.qcmserver.data.response.QcmResult;
import com.polytech.qcm.server.qcmserver.data.response.QuestionResult;
import com.polytech.qcm.server.qcmserver.exception.BadRequestException;
import com.polytech.qcm.server.qcmserver.exception.NotFoundException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QcmServerApplicationTests {

	private static final Gson GSON = new Gson();
	private static final String PASSWORD = "password";
	private static final String STUDENT_USERNAME = "student";
	private static final String TEACHER_USERNAME = "Teacher";
	private static final Principal STUDENT_PRINCIPAL = new PrincipalUser(STUDENT_USERNAME);
	private static final Principal TEACHER_PRINCIPAL = new PrincipalUser(TEACHER_USERNAME);

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private QcmRepository qcmRepository;

	@Autowired
	private AuthController authController;
	@Autowired
	private QcmController qcmController;
	@Autowired
	private ResponseController responseController;
	@Autowired
	private ResponseRepository responseRepository;

	private QCM qcm;
	private boolean initialized = false;

	@Before
	public void initData() throws IOException {
		if (initialized) {
			return;
		}
		String password = passwordEncoder.encode(PASSWORD);
		responseRepository.deleteAll();
		qcmRepository.deleteAll();
		userRepository.deleteAll();

		User teacher = new User(TEACHER_USERNAME, password, Role.TEACHER);
		userRepository.save(new User(STUDENT_USERNAME, password, Role.STUDENT));
		userRepository.save(teacher);
		userRepository.flush();

		QCM qcm = fromJson(QCM.class, "/qcm.json");
		qcm.setAuthor(teacher);
		qcm.updateReferences();
		this.qcm = qcmRepository.saveAndFlush(qcm);
		initialized = true;
	}

	@After
	public void reInit() {
		responseRepository.deleteAll();
	}

	private <T> T fromJson(Class<T> tClass, String path) throws IOException  {
		try (InputStreamReader reader = new InputStreamReader(QcmServerApplicationTests.class.getResourceAsStream(path))){
			return GSON.fromJson(reader, tClass);
		}
	}

	@Test
	public void logInTest() {
		AuthResponse response = authController.login(new User(STUDENT_USERNAME, PASSWORD, Role.STUDENT)).getBody();
		assertNotNull(response);
		assertEquals(response.getRole(), Role.STUDENT.name());
		assertEquals(response.getUsername(), STUDENT_USERNAME);

		response = authController.login(new User(TEACHER_USERNAME, PASSWORD, Role.TEACHER)).getBody();
		assertNotNull(response);
		assertEquals(response.getRole(), Role.TEACHER.name());
		assertEquals(response.getUsername(), TEACHER_USERNAME);
	}


	@Test
	public void getQcmTest() {
		List<QCM> qcms = qcmController.getAll(TEACHER_PRINCIPAL).getBody();
		assertNotNull(qcms);
		assertEquals("There should be one qcm in list" + qcms, 1, qcms.size());
		assertEquals(qcms.get(0), qcmController.getById(TEACHER_PRINCIPAL, qcms.get(0).getId()).getBody());
	}

	@Test(expected = NotFoundException.class)
	public void getUnexistingQcmTest() {
		qcmController.getById(TEACHER_PRINCIPAL, 0);
	}

	@Test
	public void newQcmUpdateAndDeleteTest() {
		QCM qcm  = qcmController.newQvm(STUDENT_PRINCIPAL).getBody();

		int oldId = qcm.getId();

		qcm = qcmController.update(STUDENT_PRINCIPAL, qcm, qcm.getId()).getBody();

		assertEquals("Update changed the id of qcm", oldId, qcm.getId().intValue());

		qcmController.delete(STUDENT_PRINCIPAL, qcm.getId());

		List<QCM> qcms = qcmController.getAll(STUDENT_PRINCIPAL).getBody();
		assertEquals("There should be one qcm in list" + qcms, 1, qcms.size());
	}


	@Test
	public void rightAnswerTest() {
		ChoiceIds rightChoiceIds = new ChoiceIds();
		List<Question> questions = qcm.getQuestions();
		rightChoiceIds.setIds(
			Stream.of(questions.get(0).getChoices().get(0), questions.get(1).getChoices().get(1))
				.map(Choice::getId)
				.collect(Collectors.toSet()));

		responseController.postResponse(STUDENT_PRINCIPAL, rightChoiceIds);

		QcmResult result = qcmController.qcmResult(STUDENT_PRINCIPAL, qcm.getId()).getBody();

		assertNotNull(result);
		assertEquals(Collections.singletonList(STUDENT_USERNAME), result.getParticipants());
		assertEquals(2, result.getQuestionResults().size());

		for (QuestionResult qr : result.getQuestionResults()) {
			Map<String, Boolean> responsesMap = qr.getReponses();
			assertEquals(1, responsesMap.size());
			assertTrue(responsesMap.containsKey(STUDENT_USERNAME));
			assertTrue(responsesMap.get(STUDENT_USERNAME));
		}
	}

	@Test
	public void wrongAnswerTest() {
		ChoiceIds wrongAnswers = new ChoiceIds();
		List<Question> questions = qcm.getQuestions();
		wrongAnswers.setIds(
			Stream.of(questions.get(0).getChoices().get(0),
				questions.get(0).getChoices().get(1),
				questions.get(1).getChoices().get(0))
				.map(Choice::getId)
				.collect(Collectors.toSet()));

		responseController.postResponse(STUDENT_PRINCIPAL, wrongAnswers);

		QcmResult result = qcmController.qcmResult(STUDENT_PRINCIPAL, qcm.getId()).getBody();

		assertNotNull(result);
		assertEquals(Collections.singletonList(STUDENT_USERNAME), result.getParticipants());
		assertEquals(2, result.getQuestionResults().size());

		for (QuestionResult qr : result.getQuestionResults()) {
			Map<String, Boolean> responsesMap = qr.getReponses();
			assertEquals(1, responsesMap.size());
			assertTrue(responsesMap.containsKey(STUDENT_USERNAME));
			assertFalse(responsesMap.get(STUDENT_USERNAME));
		}
	}

	@Test(expected = BadRequestException.class)
	public void answerTwiceTest() {
		ChoiceIds choiceIds = new ChoiceIds();

		choiceIds.setIds(Collections.singleton(qcm.getQuestions().get(0).getChoices().get(0).getId()));

		responseController.postResponse(STUDENT_PRINCIPAL, choiceIds);
		responseController.postResponse(STUDENT_PRINCIPAL, choiceIds);
	}

}
