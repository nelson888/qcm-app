package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

  private final UserRepository userRepository;
  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;
  private final PasswordEncoder passwordEncoder;

  public ApplicationConfiguration(UserRepository userRepository, QcmRepository qcmRepository, QuestionRepository questionRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostConstruct
  public void initDatabase() {
    String student = Role.STUDENT.roleName();
    String teacher = Role.TEACHER.roleName();

    User teacherUser = new User("teacher", passwordEncoder.encode("teacher"), teacher);
    Arrays.asList(
      new User("nelson", passwordEncoder.encode("nelson"), student),
      new User("nicolas", passwordEncoder.encode("nicolas"), student),
        teacherUser)
      .forEach(userRepository::save);

    List<Question> questions = new ArrayList<>();
    QCM qcm = qcmRepository.save(new QCM(0, "Test QCM", teacherUser, State.STARTED, questions));
    questions.forEach(q -> q.setQcm(qcm));
    questions.addAll(Arrays.asList(
      new Question(0, "What is life?", qcm),
      new Question(1, "What is love?", qcm),
      new Question(2, "What is?", qcm)));

    questionRepository.saveAll(questions);

  }

}
