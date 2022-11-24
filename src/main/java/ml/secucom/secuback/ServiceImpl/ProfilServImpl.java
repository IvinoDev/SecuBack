package ml.secucom.secuback.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;
import ml.secucom.secuback.Repository.ProfilRepository;
import ml.secucom.secuback.Repository.RoleRepository;
import ml.secucom.secuback.Service.ProfilService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfilServImpl implements ProfilService, UserDetailsService {

   private final ProfilRepository profilRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Profil profil = profilRepository.findByUsername(username);
        if (profil == null) {
            log.error("Collaborator not found in our database.");
            throw new UsernameNotFoundException("Collaborator not found in our database.");
        } else {
            log.info("Collaborator found in our database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        profil.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(profil.getUsername(),
                profil.getPassword(), authorities);
    }

    @Override
    public Profil saveProfil(Profil profil) {
        log.info("saving {} as a new collaborator with success.", profil.getUsername());
        profil.setPassword(passwordEncoder.encode(profil.getPassword()));
        return profilRepository.save(profil);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("adding {} as a new role with success.", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public Profil editProfil(Profil profil, Long id) {
        Profil currentprofil = profilRepository.findById(id).orElse(null);
        if (currentprofil != null) {
            profil.setPassword(passwordEncoder.encode(profil.getPassword()));
            return profilRepository.save(profil);
        } else {
            return null;
        }
    }

    @Override
    public Object deleteProfil(Long id) {
        Profil currentprofil = profilRepository.findById(id).orElse(null);
        if (currentprofil != null) {
            profilRepository.deleteById(id);
        } else {
            return null;
        }
        return "Sorry you can't do this action";
    }

    @Override
    public Profil addRoleToProfil(String username, String roleName) {
        log.info("adding {} as a role to {} with success.", roleName, username);
        Profil profil = profilRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        System.out.println(role);
        profil.getRoles().add(role);
        System.out.println(profil.getRoles());
        return profilRepository.save(profil);

        //l'annotation transactionnal va s'occuper de la sauvegarde dans la base de donnees
    }

    @Override
    public Profil getProfil(String username) {
        log.info("fetching collaborator {} with success.", username);
        return profilRepository.findByUsername(username);
    }

    @Override
    public List<Profil> getProfils() {
        log.info("fetching all the collaborators with success.");
        return profilRepository.findAll();
    }

}
