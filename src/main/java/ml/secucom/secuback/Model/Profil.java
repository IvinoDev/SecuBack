package ml.secucom.secuback.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String fname;
    @Column(nullable = false, length = 40)
    private String lname;
    @Column(nullable = false, length = 50, unique = true)
    private String username;
    @Column(nullable = false, length = 30)
    private String password;
    @Column(nullable = false, length = 100)
    private String email;
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<Role> roles = new ArrayList<>();
}


