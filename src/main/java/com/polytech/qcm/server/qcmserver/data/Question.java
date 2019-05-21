package com.polytech.qcm.server.qcmserver.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "question")
@Entity
public class Question {

    @Id
    @NonNull
    private int id;

    @NonNull
    @NotBlank
    private String question;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private QCM qcm_id;

}
