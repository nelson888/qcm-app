package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
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
  private final ChoiceRepository choiceRepository;
  private final PasswordEncoder passwordEncoder;

  public ApplicationConfiguration(UserRepository userRepository, QcmRepository qcmRepository, QuestionRepository questionRepository, ChoiceRepository choiceRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.choiceRepository = choiceRepository;
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
      .forEach(userRepository::saveAndFlush);

    List<Question> questions = new ArrayList<>();
    QCM qcm = qcmRepository.saveAndFlush(new QCM(0, "Test QCM", teacherUser, State.STARTED, questions));

    questions.addAll(questionRepository.saveAll(
      Arrays.asList(
        new Question(0, "What is life?", new ArrayList<>(), qcm),
        new Question(1, "What is love?", new ArrayList<>(), qcm),
        new Question(2, "What is something?", new ArrayList<>(), qcm))));
    questionRepository.flush();
    choiceRepository.saveAll(
      Arrays.asList(
        new Choice(0, "life itself", true, questions.get(0)),
        new Choice(1, "nothing", false, questions.get(0)),
        new Choice(2, "something", false, questions.get(0)),
        new Choice(3, "life", false, questions.get(1)),
        new Choice(4, "everything", true, questions.get(1)),
        new Choice(5, "nothing without you", false, questions.get(1)),
        new Choice(6, "some", false, questions.get(2)),
        new Choice(7, "thing", false, questions.get(2)),
        new Choice(8, "anything", true, questions.get(2))
      )
    );
    choiceRepository.flush();

  }

}
