package ml.secucom.secuback.Model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Profil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50, unique = true)
    private String username;
    @Column(nullable = false, length = 30)
    private String password;
    @Column(nullable = false, length = 100)
    private String email;
    @Enumerated(EnumType.STRING)
    private Type type;
}
