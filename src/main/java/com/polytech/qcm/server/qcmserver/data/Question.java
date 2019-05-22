package com.polytech.qcm.server.qcmserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@AllArgsConstructor
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

    @OneToMany(mappedBy = "question")
    private List<Choice> choices;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qcm_id", nullable = false)
    @JsonIgnore
    private QCM qcm;

}
