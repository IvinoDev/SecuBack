package ml.secucom.secuback.Controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;
import ml.secucom.secuback.Repository.ProfilRepository;
import ml.secucom.secuback.Repository.RoleRepository;
import ml.secucom.secuback.Service.ProfilService;
import ml.secucom.secuback.Service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/secuback")
@RequiredArgsConstructor
public class MainController {
    private final ProfilRepository profilRepository;
    private final ProfilService profilService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @GetMapping("/collaborator/all")
    public ResponseEntity<List<Profil>> getAllProfils() {
        return ResponseEntity.ok().body(profilService.getProfils());
    }

    @PostMapping("/collaborator/add")
    public ResponseEntity<Profil> saveProfils(Profil profil) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/secuback/collaborators/add").toUriString());
        return ResponseEntity.created(uri).body(profilService.saveProfil(profil));
    }

    @PostMapping("/role/add")
    public ResponseEntity<Role> saveProfils(Role role) {
        return ResponseEntity.ok().body(profilService.saveRole(role));
    }


    @PostMapping("/role/addToCollaborator")
    public ResponseEntity<?> addRoleToCollaborator(@RequestBody RoleToCollabForm form) {
        profilService.addRoleToProfil(form.getUsername(), form.getPassword());
        return ResponseEntity.ok().build();
    }
}

@Data
class RoleToCollabForm {
    private String username;
    private String password;

}