package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Data
@Table(name = "qcm")
@Entity
public class QCM {

    private static final String ALPHABETIC_REGEX = "[a-zA-Z]+";

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NonNull
    private String name;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "username", nullable = false)
    @JsonIgnore
    private User author;

    @NonNull
    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "qcm", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Question> questions;

    public QCM(@NonNull String name, @NonNull User author, @NonNull State state, List<Question> questions) {
        this.name = name;
        this.author = author;
        this.state = state;
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        List<Question> uniqueQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question.getId() == null || !uniqueQuestions.contains(question) && uniqueQuestions.stream().noneMatch(q -> q.getQuestion().equals(question.getQuestion()))) {
                uniqueQuestions.add(question);
            }
        }
        return uniqueQuestions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        updateReferences();
    }

    public void updateReferences() {
        for (Question question : questions) {
            question.setQcm(this);
            question.getChoices()
              .forEach(c -> c.setQuestion(question));
        }
    }

    @Override
    public String toString() {
        return "QCM{" +
          "id=" + id +
          ", name='" + name + '\'' +
          ", author=" + author +
          ", state=" + state +
          '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QCM qcm = (QCM) o;
        return Objects.equals(id, qcm.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
