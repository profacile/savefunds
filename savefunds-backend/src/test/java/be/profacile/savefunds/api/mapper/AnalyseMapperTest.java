package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.AnalyseResponse;
import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyseMapperTest {

    private final AnalyseMapper mapper = Mappers.getMapper(AnalyseMapper.class);

    @Test
    @DisplayName("Devrait mapper AnalysePrelevement → AnalyseResponse")
    void shouldMapEntityToResponse() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(42L);

        AnalysePrelevement analyse = AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build();
        // Simuler l'ID généré
        analyse.setId(1L);

        AnalyseResponse response = mapper.toResponse(analyse);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEntrepriseId()).isEqualTo(42L);    // entreprise.id → entrepriseId
        assertThat(response.getMontantSouhaite()).isEqualByComparingTo("5000.00");
        assertThat(response.getStatut()).isEqualTo(StatutAnalyse.EN_ATTENTE);
    }

    @Test
    @DisplayName("Devrait retourner null si entité null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("updateFromEntity ne doit pas écraser les champs null")
    void shouldNotOverwriteWithNullOnUpdate() {
        AnalysePrelevement target = AnalysePrelevement.builder()
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build();

        AnalysePrelevement source = new AnalysePrelevement();
        // source a tout à null

        mapper.updateFromEntity(source, target);

        // Champs null dans source → target inchangé
        assertThat(target.getMontantSouhaite()).isEqualByComparingTo("5000.00");
        assertThat(target.getStatut()).isEqualTo(StatutAnalyse.EN_ATTENTE);
    }
}