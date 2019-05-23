package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    // =FIXME qcm repository returns duplicated filter
    public List<Question> getQuestions() {
        List<Question> uniqueQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (!uniqueQuestions.contains(question)) {
                uniqueQuestions.add(question);
            }
        }
        return uniqueQuestions;
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
}
