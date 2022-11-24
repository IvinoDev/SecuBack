package ml.secucom.secuback.Service;

import ml.secucom.secuback.Model.Role;

import java.util.List;

public interface RoleService {
    // Mettre à jour l'Role
    Role updateRole(Role role);

    // Suprimer un Role
    void deleteRole(long id);

    // Recuperer tout les Roles
    List<Role> RolesList();
}
