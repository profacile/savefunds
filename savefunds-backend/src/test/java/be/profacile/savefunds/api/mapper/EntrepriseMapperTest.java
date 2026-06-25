package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.CreateEntrepriseRequest;
import be.profacile.savefunds.api.dto.request.UpdateEntrepriseRequest;
import be.profacile.savefunds.api.dto.response.EntrepriseResponse;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EntrepriseMapperTest {

    private final EntrepriseMapper mapper = Mappers.getMapper(EntrepriseMapper.class);

    @Test
    @DisplayName("Devrait mapper Entreprise → EntrepriseResponse")
    void shouldMapEntityToResponse() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(1L);
        entreprise.setUserId(10L);
        entreprise.setRaisonSociale("Profacile SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);

        EntrepriseResponse response = mapper.toResponse(entreprise);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getRaisonSociale()).isEqualTo("Profacile SRL");
        assertThat(response.getNumeroEntreprise()).isEqualTo("BE0123456789");
        assertThat(response.getChiffreAffairesMensuel()).isEqualByComparingTo("50000.00");
        assertThat(response.getChargesMensuelles()).isEqualByComparingTo("30000.00");
        assertThat(response.getStatut()).isEqualTo(StatutEntreprise.ACTIVE);
    }

    @Test
    @DisplayName("Devrait mapper CreateEntrepriseRequest → Entreprise")
    void shouldMapCreateRequestToEntity() {
        CreateEntrepriseRequest request = new CreateEntrepriseRequest();
        request.setUserId(10L);
        request.setRaisonSociale("Profacile SRL");
        request.setNumeroEntreprise("BE0123456789");
        request.setTresorerie(new BigDecimal("100000.00"));

        Entreprise entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNull();          // ignoré
        assertThat(entity.getStatut()).isEqualTo(StatutEntreprise.EN_CREATION);
        assertThat(entity.getCreatedAt()).isNull();   // ignoré
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getRaisonSociale()).isEqualTo("Profacile SRL");
        assertThat(entity.getTresorerie()).isEqualByComparingTo("100000.00");
    }

    @Test
    @DisplayName("Devrait mapper UpdateEntrepriseRequest → Entreprise sans toucher aux champs protégés")
    void shouldMapUpdateRequestToEntityIgnoringProtectedFields() {
        UpdateEntrepriseRequest request = new UpdateEntrepriseRequest();
        request.setRaisonSociale("Nouvelle Raison Sociale");
        request.setFormeJuridique("SA");

        Entreprise entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNull();               // ignoré
        assertThat(entity.getUserId()).isNull();           // ignoré
        assertThat(entity.getNumeroEntreprise()).isNull(); // ignoré
        assertThat(entity.getRaisonSociale()).isEqualTo("Nouvelle Raison Sociale");
        assertThat(entity.getFormeJuridique()).isEqualTo("SA");
    }

    @Test
    @DisplayName("updateFromEntity ne doit pas écraser les champs protégés")
    void shouldNotOverwriteProtectedFieldsOnUpdate() {
        Entreprise target = new Entreprise();
        target.setId(1L);
        target.setUserId(10L);
        target.setNumeroEntreprise("BE0123456789");
        target.setRaisonSociale("Ancienne Raison");

        Entreprise source = new Entreprise();
        source.setRaisonSociale("Nouvelle Raison");
        // id, userId, numeroEntreprise ignorés même si présents dans source
        source.setId(999L);
        source.setUserId(999L);

        mapper.updateFromEntity(source, target);

        assertThat(target.getId()).isEqualTo(1L);                    // protégé
        assertThat(target.getUserId()).isEqualTo(10L);               // protégé
        assertThat(target.getNumeroEntreprise()).isEqualTo("BE0123456789"); // protégé
        assertThat(target.getRaisonSociale()).isEqualTo("Nouvelle Raison"); // mis à jour
    }
}