package com.polytech.qcm.server.qcmserver.data;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "reponse")
@Entity
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int reponseId;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Choix choix;

}
