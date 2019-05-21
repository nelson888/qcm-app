package com.polytech.qcm.server.qcmserver.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "qcm")
@Entity
public class QCM {

    private static final String ALPHABETIC_REGEX = "[a-zA-Z]+";

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User author;

    @NonNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "question")
    private List<Question> questions;
}
