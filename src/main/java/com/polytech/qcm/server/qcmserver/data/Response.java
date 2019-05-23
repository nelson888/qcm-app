package com.polytech.qcm.server.qcmserver.data;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Table(name = "response")
@Entity
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer responseId;
    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Choice choice;

    public Response(@NonNull User user, @NonNull Choice choice) {
        this.user = user;
        this.choice = choice;
    }

    @Override
    public String toString() {
        return "Response{" +
          "user=" + user.getUsername() +
          ", choice=" + choice +
          '}';
    }
}
