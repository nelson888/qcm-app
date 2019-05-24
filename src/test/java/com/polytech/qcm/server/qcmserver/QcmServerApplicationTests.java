package com.polytech.qcm.server.qcmserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
import com.polytech.qcm.server.qcmserver.controller.AuthController;
import com.polytech.qcm.server.qcmserver.controller.QcmController;
import com.polytech.qcm.server.qcmserver.data.PrincipalUser;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.data.response.AuthResponse;
import com.polytech.qcm.server.qcmserver.exception.NotFoundException;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
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
import java.util.List;

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
	private ResponseRepository responseRepository;
	private boolean initialized = false;

	@Before
	public void initData() throws IOException {
		String password = passwordEncoder.encode(PASSWORD);
		if (initialized) {
			return;
		}
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
		qcmRepository.saveAndFlush(qcm);
		initialized = true;
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
		assertEquals(response.getRole(), Role.STUDENT.roleName());
		assertEquals(response.getUsername(), STUDENT_USERNAME);

		response = authController.login(new User(TEACHER_USERNAME, PASSWORD, Role.TEACHER)).getBody();
		assertNotNull(response);
		assertEquals(response.getRole(), Role.TEACHER.roleName());
		assertEquals(response.getUsername(), TEACHER_USERNAME);
	}


	@Test
	public void getQcmTest() {
		List<QCM> qcms = qcmController.getAll().getBody();
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

		List<QCM> qcms = qcmController.getAll().getBody();
		assertEquals("There should be one qcm in list" + qcms, 1, qcms.size());
	}

	//TODO make tests about answer
}
