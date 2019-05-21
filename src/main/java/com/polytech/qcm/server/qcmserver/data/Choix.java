package com.polytech.qcm.server.qcmserver.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "choix")
@Entity
public class Choix {

    @Id
    @NonNull
    private int id;

    @NonNull
    @NotBlank
    private String value;

    @NonNull
    private boolean isAnswer;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Question question_id;



}
