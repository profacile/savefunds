package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.CreateSituationFinanciereRequest;
import be.profacile.savefunds.api.dto.response.SituationFinanciereResponse;
import be.profacile.savefunds.domain.entity.SituationFinanciere;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SituationFinanciereMapperTest {

    private final SituationFinanciereMapper mapper = Mappers.getMapper(SituationFinanciereMapper.class);

    @Test
    @DisplayName("Devrait mapper SituationFinanciere → SituationFinanciereResponse")
    void shouldMapEntityToResponse() {
        SituationFinanciere situation = new SituationFinanciere();
        situation.setId(1L);
        situation.setEntrepriseId(10L);
        situation.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        situation.setChargesMensuelles(new BigDecimal("30000.00"));
        situation.setTresorerie(new BigDecimal("100000.00"));
        situation.setSoldeCompteCourant(new BigDecimal("5000.00"));
        situation.setRatioCACharges(new BigDecimal("1.67"));
        situation.setTresorerieEnMois(new BigDecimal("3.33"));
        situation.setDureeCompteCourantDebiteur(0);
        situation.setSource("MANUEL");
        situation.setCapturedAt(LocalDateTime.of(2026, 4, 6, 14, 30));

        SituationFinanciereResponse response = mapper.toResponse(situation);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEntrepriseId()).isEqualTo(10L);
        assertThat(response.getChiffreAffairesMensuel()).isEqualByComparingTo("50000.00");
        assertThat(response.getRatioCACharges()).isEqualByComparingTo("1.67");
        assertThat(response.getTresorerieEnMois()).isEqualByComparingTo("3.33");
        assertThat(response.getSource()).isEqualTo("MANUEL");
        assertThat(response.getCapturedAt()).isEqualTo(LocalDateTime.of(2026, 4, 6, 14, 30));
    }

    @Test
    @DisplayName("Devrait mapper CreateRequest → Entity en ignorant les champs calculés")
    void shouldMapCreateRequestToEntityIgnoringCalculatedFields() {
        CreateSituationFinanciereRequest request = new CreateSituationFinanciereRequest();
        request.setEntrepriseId(10L);
        request.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        request.setChargesMensuelles(new BigDecimal("30000.00"));
        request.setTresorerie(new BigDecimal("100000.00"));
        request.setSoldeCompteCourant(new BigDecimal("5000.00"));
        request.setSource("MANUEL");

        SituationFinanciere entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNull();                        // ignoré
        assertThat(entity.getCapturedAt()).isNotNull(); // défini par le constructeur de l'entité
        assertThat(entity.getRatioCACharges()).isNull();           // ignoré — calculé
        assertThat(entity.getTresorerieEnMois()).isNull();         // ignoré — calculé
        assertThat(entity.getEntrepriseId()).isEqualTo(10L);
        assertThat(entity.getChiffreAffairesMensuel()).isEqualByComparingTo("50000.00");
        assertThat(entity.getSource()).isEqualTo("MANUEL");
    }
}