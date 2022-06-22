package com.JS.Ersatzteildatenbank.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.JS.Ersatzteildatenbank.model.Land;
import com.JS.Ersatzteildatenbank.repository.LandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerTypePredicate;

import java.util.Optional;

// Allows CORS in react
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/land")
public class LandController {
    @Autowired
    private LandRepository landRepository;
    /*
     * 200 -> Länder gefunden
     * 204 -> kein Land gefunden
     * 500 -> Internal Server Error
     */
    @GetMapping
    public ResponseEntity<?> getAllLaender() {
        try {
            if (landRepository.findAll().isEmpty()) {
                return new ResponseEntity<>
                        ("Keine Laender gefunden.", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(landRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
     * 200 -> Land gefunden
     * 204 -> Land existiert nicht
     * 400 -> falsches Format
     * 500 -> Internal Server Error
     */
    @GetMapping("/{iso}")
    public ResponseEntity<?> getLandByIso_3166_2(@PathVariable("iso")String iso) {
        try {
            if(!iso.matches("[A-Z][A-Z]"))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            Optional<Land> land = Optional.ofNullable(landRepository.findByIso_3166_2(iso));
            if(land.isPresent()) return new ResponseEntity<>(land, HttpStatus.OK);
            else return new ResponseEntity<>
                    ("Land mit ISO-3166-2 \"" + iso + "\" existiert nicht.", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
     * 201 -> Land erstellt
     * 204 -> Land existiert nicht
     * 400 -> falsches Format
     * 500 -> Internal Server Error
     */
    @PostMapping
    public ResponseEntity<?> createLand(@Validated @RequestBody Land land) {
        try {
            if(!land.getIso_3166_2().matches("[A-Z][A-Z]") && !land.getName().matches("[A-ZÄÖÜ][a-zäöü]+}")) {
                return new ResponseEntity<>("Falsches Format",HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(landRepository.save(land), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
     * 200 -> erfolgreich aktualisiert
     * 204 -> Land existiert nicht
     * 400 -> falsches Format
     * 500 -> Internal Server Error
     */
    @PutMapping("/{iso}")
    public ResponseEntity<?> updateLandByIso_3166_2
        (@PathVariable("iso") String iso, @Validated @RequestBody Land land) {
        try {
            if(!iso.matches("[A-Z][A-Z]")) {
                return new ResponseEntity<>("Falsches ISO-Format in der URL",HttpStatus.BAD_REQUEST);
            } else if (land.getIso_3166_2() == null || land.getName() == null){
                return new ResponseEntity<>("Land-Attribute duerfen nicht 'null' sein.", HttpStatus.BAD_REQUEST);
            } else if (!land.getIso_3166_2().matches("[A-Z][A-Z]") &&
            !land.getName().matches("[A-ZÄÖÜ][a-zäöü]+")) {
                return new ResponseEntity<>("ISO- oder Name-Format falsch", HttpStatus.BAD_REQUEST);
            } else if (Optional.ofNullable(landRepository.findByIso_3166_2(iso)).isEmpty()) {
                return new ResponseEntity<>("Land mit ISO '" + iso + "' existiert nicht.",HttpStatus.NO_CONTENT);
            } else {
                Land _land = landRepository.findByIso_3166_2(iso);
                _land.setIso_3166_2(land.getIso_3166_2());
                _land.setName(land.getName());
                return new ResponseEntity<>(landRepository.save(_land), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
     * 204 -> Land erfolgreich geloescht,Land mit id existiert nicht
     * 400 -> falsches Iso-Format in URL
     * 500 -> Internal Server Error
     */
    @DeleteMapping("/{iso}")
    public ResponseEntity<?> deleteLandByIso_3166_2(@PathVariable("iso") String iso) {
        try {
            if(!iso.matches("[A-Z][A-Z]")) {
                return new ResponseEntity<>("Falsches ISO-Format in der URL.", HttpStatus.BAD_REQUEST);
            }
            Optional<Land> land = Optional.ofNullable(landRepository.findByIso_3166_2(iso));
            if (land.isEmpty()) {
                return new ResponseEntity<>("Land mit Iso " + iso + " existiert nicht.", HttpStatus.NO_CONTENT);
            } else {
                Land deletion = land.get();
                landRepository.delete(deletion);
                return new ResponseEntity<>("Land mit Iso " + iso + " erfolgreich entfernt.", HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*
     * 204 -> Laender erfolgreich entfernt
     * 500 -> Internal Server Error
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAllLaender() {
        return new ResponseEntity<>("Alle Laender entfernt.", HttpStatus.NO_CONTENT);
    }
}
