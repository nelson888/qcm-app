package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@Table(name = "choice")
@Entity
public class Choice {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NonNull
    @NotBlank
    private String value;

    @NonNull
    private boolean isAnswer;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    public Choice(@NonNull @NotBlank String value, @NonNull boolean isAnswer, @NonNull Question question) {
        this.value = value;
        this.isAnswer = isAnswer;
        this.question = question;
    }

    @Override
    public String toString() {
        return "Choice{" +
          "id=" + id +
          ", value='" + value + '\'' +
          ", isAnswer=" + isAnswer +
          '}';
    }
}
