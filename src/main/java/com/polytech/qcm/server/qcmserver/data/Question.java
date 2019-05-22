package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@Data
@Table(name = "question")
@Entity
public class Question {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NonNull
    @NotBlank
    private String question;

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
}
