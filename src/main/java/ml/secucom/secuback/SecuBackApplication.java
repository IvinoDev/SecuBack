package ml.secucom.secuback;

import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;
import ml.secucom.secuback.Model.Type;
import ml.secucom.secuback.Repository.ProfilRepository;
import ml.secucom.secuback.Service.ProfilService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;

@SpringBootApplication
public class SecuBackApplication {

    public static void main(String[] args) {

        SpringApplication.run(SecuBackApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    };

    @Bean
    CommandLineRunner run(ProfilService profilService, ProfilRepository profilRepository) {
        return args -> {
            Profil newProfilAdmin = profilRepository.findByUsername("MKDev");
            Profil newProfilUser = profilRepository.findByUsername("BabaBallo");
            if(newProfilUser == null && newProfilUser == null) {
                profilService.saveRole(new Role(null, "ROLE_USER"));
                profilService.saveRole(new Role(null, "ROLE_ADMIN"));
                profilService.saveProfil(new Profil(
                        null,
                        "Kayantao",
                        "Mariam",
                        "MKDev",
                        "Sh123456",
                        "mkdev@orange.ml", Type.ADMIN, new ArrayList<>()));
                profilService.saveProfil(new Profil(null,
                        "Ballo",
                        "Ibrahima",
                        "BabaBallo",
                        "Bb23456",
                        "iballo@orange.ml", Type.USER, new ArrayList<>()));

                profilService.addRoleToProfil("MKDev", "ROLE_ADMIN");
                profilService.addRoleToProfil("MKDev", "ROLE_USER");

                profilService.addRoleToProfil("BabaBallo", "ROLE_USER");
            }
                System.out.println("App launch with our default users is okay");
        };
    }
}
