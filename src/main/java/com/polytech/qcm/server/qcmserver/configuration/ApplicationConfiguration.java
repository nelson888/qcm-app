package com.polytech.qcm.server.qcmserver.configuration;

import com.polytech.qcm.server.qcmserver.data.Choice;
import com.polytech.qcm.server.qcmserver.data.QCM;
import com.polytech.qcm.server.qcmserver.data.Question;
import com.polytech.qcm.server.qcmserver.data.Response;
import com.polytech.qcm.server.qcmserver.data.Role;
import com.polytech.qcm.server.qcmserver.data.State;
import com.polytech.qcm.server.qcmserver.data.User;
import com.polytech.qcm.server.qcmserver.repository.ChoiceRepository;
import com.polytech.qcm.server.qcmserver.repository.QcmRepository;
import com.polytech.qcm.server.qcmserver.repository.QuestionRepository;
import com.polytech.qcm.server.qcmserver.repository.ResponseRepository;
import com.polytech.qcm.server.qcmserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableSwagger2
public class ApplicationConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

  private final UserRepository userRepository;
  private final QcmRepository qcmRepository;
  private final QuestionRepository questionRepository;
  private final ChoiceRepository choiceRepository;
  private final ResponseRepository responseRepository;
  private final PasswordEncoder passwordEncoder;
  @Value("${spring.profiles.active}")
  private String activeProfile;

  public ApplicationConfiguration(UserRepository userRepository, QcmRepository qcmRepository, QuestionRepository questionRepository, ChoiceRepository choiceRepository, ResponseRepository responseRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.qcmRepository = qcmRepository;
    this.questionRepository = questionRepository;
    this.choiceRepository = choiceRepository;
    this.responseRepository = responseRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  Map<Integer, Integer> currentQuestionMap() {
    return new ConcurrentHashMap<>();
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any())
      .build();
  }

  @PostConstruct
  public void initDatabase() {
    if (!activeProfile.equals("local")) {
      return;
    }
    LOGGER.info("Clearing repositories...");
    Stream.of( // ORDER OF DELETE MATTERS!!!
      choiceRepository,
      questionRepository,
      responseRepository,
      qcmRepository,
      userRepository
      ).forEach(CrudRepository::deleteAll);

    LOGGER.info("Inserting fake data...");

    String student = Role.STUDENT.roleName();
    String teacher = Role.TEACHER.roleName();

    User teacherUser = new User("teacher", passwordEncoder.encode("teacher"), teacher);
    List<User> users = Arrays.asList(
      new User("nelson", passwordEncoder.encode("nelson"), student),
      new User("nicolas", passwordEncoder.encode("nicolas"), student),
      new User("teacher2", passwordEncoder.encode("teacher2"), teacher),
      new User("admin", passwordEncoder.encode("admin"), Role.ADMIN.roleName()),
        teacherUser);
    users.forEach(userRepository::saveAndFlush);

    List<Question> questions = new ArrayList<>();
    QCM qcm = new QCM("Test QCM", teacherUser, State.COMPLETE, questions);

    questions.addAll((
      Arrays.asList(
        new Question("What is life?", new ArrayList<>(), qcm),
        new Question("What is love?", new ArrayList<>(), qcm),
        new Question("What is something?", new ArrayList<>(), qcm))));

    qcmRepository.saveAndFlush(qcm);
    questions = questionRepository.findAll();

    List<Choice> choices = choiceRepository.saveAll(
      Arrays.asList(
        new Choice("life itself", true, questions.get(0)),
        new Choice("nothing", false, questions.get(0)),
        new Choice("something", false, questions.get(0)),
        new Choice("life", false, questions.get(1)),
        new Choice("everything", true, questions.get(1)),
        new Choice("nothing without you", false, questions.get(1)),
        new Choice("some", false, questions.get(2)),
        new Choice("thing", false, questions.get(2)),
        new Choice("anything", true, questions.get(2))
      )
    );
    choiceRepository.flush();

    responseRepository.saveAll(
      Arrays.asList(
        new Response(users.get(0), choices.get(0)),
        new Response(users.get(1), choices.get(2)),
        new Response(users.get(1), choices.get(3)),
        new Response(users.get(0), choices.get(8))
      ));
    responseRepository.flush();

    LOGGER.info("Created users {}", users.stream().map(User::getUsername).collect(Collectors.toList()));
    LOGGER.info("Created {}", qcm);
    for (Question question : questionRepository.findAll()) {
      LOGGER.info("Created {}", question);
    }
    for (Choice choice : choiceRepository.findAll()) {
      LOGGER.info("Created {}", choice);
    }

    for (Response response : responseRepository.findAll()) {
      LOGGER.info("Created {}", response);
    }



  }

}
