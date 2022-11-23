package ml.secucom.secuback.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;
import ml.secucom.secuback.Repository.ProfilRepository;
import ml.secucom.secuback.Repository.RoleRepository;
import ml.secucom.secuback.Service.ProfilService;
import ml.secucom.secuback.Service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
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

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                Profil profil = profilService.getProfil(username);
                String access_token = JWT.create()
                        .withSubject(profil.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 5000))
                        //Pour ajouter le nom de l'api contenu dans l'url en tant que signateur du JWT
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",
                                profil.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                //Pour retourner les infos des deux tokens dans les headers
                response.setHeader("access_token", access_token);
                response.setHeader("refresh_token", refresh_token);

                //pour retourner les infos des deux tokens dans le body
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                log.info("the current content: {}", response.getOutputStream());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception exception) {
                log.error("error logging in: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("refresh token is missing");
        }
    }
}

@Data
class RoleToCollabForm {
    private String username;
    private String password;

}