package ml.secucom.secuback.Repository;

import ml.secucom.secuback.Model.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilRepository extends JpaRepository<Profil, Long> {
    Profil findByUsername(String username);

}
