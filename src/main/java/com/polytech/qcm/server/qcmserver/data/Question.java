package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Data
@Table(name = "question")
@Entity
public class Question implements Comparable<Question> {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonIgnore
    private int qcmIndex;

    @NonNull
    @NotBlank
    private String question;

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("questionIndex")
    private List<Choice> choices;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "qcm_id", nullable = false)
    @JsonIgnore
    private QCM qcm;

    public Question(@NonNull @NotBlank String question, List<Choice> choices, @NonNull QCM qcm) {
        this.question = question;
        this.choices = choices;
        this.qcm = qcm;
    }

    @Override
    public String toString() {
        return "Question{" +
          "id=" + id +
          ", question='" + question + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Question question) {
        return question.qcmIndex - qcmIndex;
    }
}
