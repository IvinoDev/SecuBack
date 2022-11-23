package ml.secucom.secuback.Service;

import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;

import java.util.List;

public interface ProfilService {

    Profil saveProfil (Profil profil);

    Role saveRole(Role role);

    //fonction qui se contente d'ajouter un role a un profil, donc elle ne retourne rien
    void addRoleToProfil (String username, String roleName);

    //fonction qui retrouve un profil via le nom d'utilisateur, qui est unique
    Profil getProfil (String username);

    //Pour recuperer la liste de tous les profils
    List<Profil>getProfils();
}
