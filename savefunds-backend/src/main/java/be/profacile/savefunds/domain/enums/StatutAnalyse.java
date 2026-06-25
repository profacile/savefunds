package be.profacile.savefunds.domain.enums;

/**
 * Statuts possibles d'une analyse de prélèvement.
 *
 * Cycle de vie simple :
 * 1. EN_ATTENTE : Analyse créée, en attente de calcul
 * 2. TERMINEE : Analyse calculée, résultat disponible dans ResultatAnalyse
 * 3. ANNULEE : Analyse annulée par l'utilisateur
 *
 * Note : La décision finale (VERT/ORANGE/ROUGE) est stockée dans
 * ResultatAnalyse.decisionGlobale, PAS dans ce statut.
 */
public enum StatutAnalyse {

    /**
     * L'analyse a été créée mais n'a pas encore été effectuée.
     *
     * État initial lors de la création (POST /api/v1/analyses).
     * L'utilisateur peut la modifier ou la lancer.
     *
     * Transitions possibles :
     * - → TERMINEE (après POST /api/v1/analyses/{id}/analyser)
     * - → ANNULEE (si l'utilisateur annule)
     */
    EN_ATTENTE,

    /**
     * L'analyse a été effectuée avec succès.
     *
     * Un ResultatAnalyse existe dans la base avec :
     * - La décision globale (VERT/ORANGE/ROUGE)
     * - Les scores calculés
     * - Les recommandations détaillées
     *
     * Cet état est FINAL (plus de modification possible).
     *
     * Transitions possibles :
     * - Aucune (état terminal)
     */
    TERMINEE,

    /**
     * L'analyse a été annulée par l'utilisateur.
     *
     * Aucun résultat n'a été calculé.
     * L'analyse reste visible dans l'historique mais inactive.
     *
     * Cet état est FINAL.
     *
     * Transitions possibles :
     * - Aucune (état terminal)
     */
    ANNULEE
}