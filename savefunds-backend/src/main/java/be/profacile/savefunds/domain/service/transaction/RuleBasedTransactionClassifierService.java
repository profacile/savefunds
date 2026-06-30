package be.profacile.savefunds.domain.service.transaction;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.TransactionClassificationType;
import be.profacile.savefunds.domain.enums.TransactionReviewStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RuleBasedTransactionClassifierService implements TransactionClassifierService {

    private static final List<String> PRIVATE_MERCHANTS = List.of(
            "carrefour", "colruyt", "delhaize", "amazon", "zalando", "ikea", "restaurant", "lunch", "hotel"
    );

    @Override
    public TransactionClassificationResult classify(
            Entreprise entreprise,
            LocalDate transactionDate,
            String description,
            BigDecimal amount
    ) {
        String text = normalize(description);
        BigDecimal absoluteAmount = amount == null ? BigDecimal.ZERO : amount.abs();

        if (amount != null && amount.signum() > 0 && containsAny(text, "remboursement dirigeant", "remboursement gerant", "remboursement associé")) {
            return result(TransactionClassificationType.REMBOURSEMENT_DIRIGEANT, TransactionReviewStatus.AUTO_CLASSIFIED, 86, true,
                    "Remboursement entrant identifie comme regularisation possible du compte courant dirigeant.");
        }

        if (containsAny(text, "salaire", "remuneration", "rémunération", "payroll", "secretariat social")) {
            return result(TransactionClassificationType.REMUNERATION, TransactionReviewStatus.AUTO_CLASSIFIED, 92, false,
                    "Libelle salarial detecte: la transaction ne doit pas alimenter le compte courant.");
        }

        if (amount != null && amount.signum() < 0 && mentionsDirector(text, entreprise)) {
            return result(TransactionClassificationType.RETRAIT_CC, TransactionReviewStatus.NEEDS_REVIEW, 78, true,
                    "Virement sortant vers une personne liee au dirigeant: impact CC probable, validation recommandee.");
        }

        if (amount != null && amount.signum() < 0 && absoluteAmount.compareTo(new BigDecimal("500")) >= 0
                && containsAny(text, "virement", "transfer", "bancontact", "retrait")) {
            return result(TransactionClassificationType.RETRAIT_CC, TransactionReviewStatus.NEEDS_REVIEW, 62, true,
                    "Montant sortant significatif avec libelle generique: retrait dirigeant possible.");
        }

        if (amount != null && amount.signum() < 0 && PRIVATE_MERCHANTS.stream().anyMatch(text::contains)) {
            return result(TransactionClassificationType.ACHAT_PRIVE_SUSPECTE, TransactionReviewStatus.NEEDS_REVIEW, 68, true,
                    "Marchand souvent mixte prive/professionnel: achat prive suspect a verifier.");
        }

        if (amount != null && amount.signum() < 0) {
            return result(TransactionClassificationType.CHARGE_PROFESSIONNELLE, TransactionReviewStatus.AUTO_CLASSIFIED, 70, false,
                    "Sortie bancaire non suspecte selon les regles metier actuelles.");
        }

        return result(TransactionClassificationType.INCERTAIN, TransactionReviewStatus.NEEDS_REVIEW, 45, false,
                "Classification incertaine: une validation humaine est necessaire.");
    }

    private TransactionClassificationResult result(
            TransactionClassificationType classification,
            TransactionReviewStatus reviewStatus,
            Integer confidence,
            boolean impactsCurrentAccount,
            String reason
    ) {
        return TransactionClassificationResult.builder()
                .classification(classification)
                .reviewStatus(reviewStatus)
                .confidenceScore(confidence)
                .impactsDirectorCurrentAccount(impactsCurrentAccount)
                .reason(reason)
                .build();
    }

    private boolean mentionsDirector(String text, Entreprise entreprise) {
        String companyName = normalize(entreprise.getRaisonSociale());
        if (!companyName.isBlank() && text.contains(companyName)) {
            return false;
        }
        return containsAny(text, "dirigeant", "gerant", "gérant", "associe", "associé", "administrateur", "prive", "privé");
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (text.contains(normalize(value))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }
}
