package be.profacile.savefunds.domain.enums;

/**
 * Statuts possibles d'une analysis de prélèvement.
 *
 * Cycle de vie simple :
 * 1. EN_ATTENTE : Analyse créée, en attente de calcul
 * 2. TERMINEE : Analyse calculée, résultat disponible dans AnalysisResult
 * 3. ANNULEE : Analyse annulée par l'utilisateur
 *
 * Note : La décision finale (VERT/ORANGE/ROUGE) est stockée dans
 * AnalysisResult.globalDecision, PAS dans ce status.
 */
public enum AnalysisStatus {

    /**
     * L'analysis a été créée mais n'a pas encore été effectuée.
     *
     * État initial lors de la création (POST /api/v1/analyses).
     * L'utilisateur peut la modifier ou la lancer.
     *
     * Transitions possibles :
     * - → TERMINEE (après POST /api/v1/analyses/{id}/result)
     * - → ANNULEE (si l'utilisateur annule)
     */
    EN_ATTENTE,

    /**
     * L'analysis a été effectuée avec succès.
     *
     * Un AnalysisResult existe dans la base avec :
     * - La décision globale (VERT/ORANGE/ROUGE)
     * - Les scores calculés
     * - Les recommendations détaillées
     *
     * Cet état est FINAL (plus de modification possible).
     *
     * Transitions possibles :
     * - Aucune (état terminal)
     */
    TERMINEE,

    /**
     * L'analysis a été annulée par l'utilisateur.
     *
     * Aucun résultat n'a été calculé.
     * L'analysis reste visible dans l'historique mais inactive.
     *
     * Cet état est FINAL.
     *
     * Transitions possibles :
     * - Aucune (état terminal)
     */
    ANNULEE
}