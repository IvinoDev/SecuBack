package ml.secucom.secuback.Service;

import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;

import java.util.List;

public interface ProfilService {
    //fonction qui creee un profil
    Profil saveProfil (Profil profil);
    //fonction qui creee un role
    Role saveRole(Role role);

    //fonction qui modifie un profil
    Profil editProfil (Profil profil, Long id);

    //fonction qui supprime un profil
    Object deleteProfil (Long id);

    //fonction qui se contente d'ajouter un role a un profil, donc elle ne retourne rien
    Profil addRoleToProfil (String username, String roleName);

    //fonction qui retrouve un profil via le nom d'utilisateur, qui est unique
    Profil getProfil (String username);

    //Pour recuperer la liste de tous les profils
    List<Profil>getProfils();
}
