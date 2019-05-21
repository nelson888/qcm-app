package com.polytech.qcm.server.qcmserver.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "qcm")
@Entity
public class QCM {

    private static final String ALPHABETIC_REGEX = "[a-zA-A]+";

    @Id
    @NonNull
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
    private String state;

    public String getState(){ return state;}

}
