package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.ResultatAnalyseResponse;
import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.enums.Decision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ResultatAnalyseMapperTest {

    private final ResultatAnalyseMapper mapper = Mappers.getMapper(ResultatAnalyseMapper.class);

    @Test
    @DisplayName("Devrait mapper ResultatAnalyse → ResultatAnalyseResponse")
    void shouldMapEntityToResponse() {
        AnalysePrelevement analyse = new AnalysePrelevement();
        analyse.setId(5L);

        ResultatAnalyse resultat = ResultatAnalyse.builder()
                .analyse(analyse)
                .decisionGlobale(Decision.VERT)
                .scoreTresorerie(new BigDecimal("3.50"))
                .scoreRatioCACharges(new BigDecimal("1.67"))
                .scoreCompteCourantDebiteur(0)
                .decisionTresorerie(Decision.VERT)
                .decisionRatioCACharges(Decision.VERT)
                .decisionCompteCourant(Decision.VERT)
                .recommandationGlobale("Situation saine.")
                .build();
        resultat.setId(1L);

        ResultatAnalyseResponse response = mapper.toResponse(resultat);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAnalyseId()).isEqualTo(5L);   // analyse.id → analyseId
        assertThat(response.getDecisionGlobale()).isEqualTo(Decision.VERT);
        assertThat(response.getScoreTresorerie()).isEqualByComparingTo("3.50");
        assertThat(response.getScoreRatioCACharges()).isEqualByComparingTo("1.67");
        assertThat(response.getScoreCompteCourantDebiteur()).isEqualTo(0);
        assertThat(response.getRecommandationGlobale()).isEqualTo("Situation saine.");
    }

    @Test
    @DisplayName("Devrait retourner null si entité null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("updateFromEntity ne doit pas modifier id ni analyse")
    void shouldNotOverwriteIdAndAnalyseOnUpdate() {
        AnalysePrelevement analyse = new AnalysePrelevement();
        analyse.setId(5L);

        ResultatAnalyse target = ResultatAnalyse.builder()
                .analyse(analyse)
                .decisionGlobale(Decision.ORANGE)
                .scoreTresorerie(new BigDecimal("2.00"))
                .build();
        target.setId(1L);

        ResultatAnalyse source = ResultatAnalyse.builder()
                .decisionGlobale(Decision.VERT)
                .scoreTresorerie(new BigDecimal("4.00"))
                .build();
        source.setId(999L); // ne doit pas écraser

        mapper.updateFromEntity(source, target);

        assertThat(target.getId()).isEqualTo(1L);              // protégé
        assertThat(target.getAnalyse().getId()).isEqualTo(5L); // protégé
        assertThat(target.getDecisionGlobale()).isEqualTo(Decision.VERT);    // mis à jour
        assertThat(target.getScoreTresorerie()).isEqualByComparingTo("4.00"); // mis à jour
    }
}