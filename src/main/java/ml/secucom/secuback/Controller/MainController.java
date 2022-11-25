package ml.secucom.secuback.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.secucom.secuback.Configuration.ResponseHandler;
import ml.secucom.secuback.Model.Profil;
import ml.secucom.secuback.Model.Role;
import ml.secucom.secuback.Model.Type;
import ml.secucom.secuback.Repository.ProfilRepository;
import ml.secucom.secuback.Repository.RoleRepository;
import ml.secucom.secuback.Service.ProfilService;
import ml.secucom.secuback.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
//@RequestMapping("/secuback")
@RequiredArgsConstructor
public class MainController {
    private final ProfilRepository profilRepository;
    private final ProfilService profilService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;


    private final OAuth2AuthorizedClientService authorizedClientService;


    @RequestMapping("/**")
    private StringBuffer getOauth2LoginInfo(Principal profil){

        StringBuffer protectedInfo = new StringBuffer();

        OAuth2AuthenticationToken authToken = ((OAuth2AuthenticationToken) profil);
        OAuth2AuthorizedClient authClient =
                this.authorizedClientService.loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());
        if(authToken.isAuthenticated()){

            Map<String,Object> userAttributes = ((DefaultOAuth2User) authToken.getPrincipal()).getAttributes();

            String userToken = authClient.getAccessToken().getTokenValue();
            protectedInfo.append("Bienvenue, " + userAttributes.get("name")+"<br><br>");
            protectedInfo.append("e-mail: " + userAttributes.get("email")+"<br><br>");
            protectedInfo.append("Access Token: " + userToken+"<br><br>");
        }
        else{
            protectedInfo.append("NA");
        }
        return protectedInfo;
    }

    @GetMapping("/collaborator/all")
    public ResponseEntity<List<Profil>> getAllProfils() {
        return ResponseEntity.ok().body(profilService.getProfils());
    }

    @GetMapping("/greetings")
    public String greetUser (@RequestBody RoleToCollabForm form){
        Profil profil = profilRepository.findByUsername(form.getUsername());
        if(profil.getType() == Type.ADMIN) {
            return "Bienvenue cher Admin !";
        } else {
            return "Bienvenue cher User";
        }
    }

    @PostMapping("/collaborator/add")
    public ResponseEntity<Profil> saveNewProfil(Profil profil) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/secuback/collaborators/add").toUriString());
        return ResponseEntity.created(uri).body(profilService.saveProfil(profil));
    }

    @PostMapping("/role/add")
    public ResponseEntity<Role> saveNewRole(Role role) {
        return ResponseEntity.ok().body(profilService.saveRole(role));
    }


    @PostMapping("/role/addToCollaborator")
    public Profil addRoleToCollaborator(@RequestBody RoleToCollabForm form) {
        return profilService.addRoleToProfil(form.getUsername(), form.getRoleName());

    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                        .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
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



    //uniquement en JSON dans Postman
    @PutMapping("/collaborator/edit/{id}")
    ResponseEntity<Object> editCollab(Profil profil,@PathVariable long id) {
        if (profilService.editProfil(profil,id) != null) {
            return ResponseHandler.generateResponse(
                    "Collaborator modified with success",
                    HttpStatus.OK,
                    profilService.editProfil(profil, id)
            );
        } else {
            return ResponseHandler.generateResponse(
                    "Sorry, this profil doesn't exist",
                    HttpStatus.BAD_REQUEST,
                    profilService.editProfil(profil, id)
            );
        }

    }
    @DeleteMapping("/collaborator/delete/{id}")
    ResponseEntity<Object> deleteCollab(@PathVariable long id) {
        if (profilService.deleteProfil(id) != null) {
            return ResponseHandler.generateResponse(
                    "Collaborator deleted with success",
                    HttpStatus.OK,
                    profilService.deleteProfil(id)
            );
        } else {
            return ResponseHandler.generateResponse(
                    "this collaborator doesn't exist",
                    HttpStatus.BAD_REQUEST,
                    profilService.deleteProfil(id)
            );
        }
    }
}

@Data
class RoleToCollabForm {
    private String username;
    private String roleName;


}