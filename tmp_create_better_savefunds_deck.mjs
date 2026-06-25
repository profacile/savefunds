import fs from "node:fs/promises";
import path from "node:path";
import { Presentation, PresentationFile } from "file:///C:/Users/SHH8701/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/@oai/artifact-tool/dist/artifact_tool.mjs";

const root = "C:\\Users\\SHH8701\\opt\\personnal\\PROFACILE_SRL\\savefunds";
const outDir = path.join(root, "output", "pptx");
const qaDir = path.join(root, "output", "pptx", "qa-savefunds-défense");
await fs.mkdir(outDir, { recursive: true });
await fs.mkdir(qaDir, { recursive: true });

const finalPptx = path.join(outDir, "SaveFunds_Defense_15_20min_Amelioree.pptx");

const W = 1280;
const H = 720;
const C = {
  navy: "#081427",
  navy2: "#10213A",
  ink: "#172033",
  muted: "#64748B",
  line: "#D8E0EA",
  bg: "#F6F8FB",
  white: "#FFFFFF",
  cyan: "#0EA5E9",
  green: "#16A34A",
  amber: "#F59E0B",
  red: "#DC2626",
  violet: "#7C3AED",
};

const deck = Presentation.create({ slideSize: { width: W, height: H } });

function addShape(slide, name, geometry, position, fill, line = { style: "solid", fill: "none", width: 0 }, extra = {}) {
  return slide.shapes.add({ name, geometry, position, fill, line, ...extra });
}

function addText(slide, text, position, style = {}, name = "text") {
  const box = addShape(slide, name, "textbox", position, "none");
  box.text = text;
  box.text.style = {
    typeface: "Aptos",
    fontSize: 22,
    color: C.ink,
    ...style,
  };
  return box;
}

function footer(slide, n, total) {
  addShape(slide, "footer-line", "rect", { left: 56, top: 660, width: 1168, height: 1 }, C.line);
  addText(slide, "SaveFunds - Défense TFE", { left: 56, top: 674, width: 420, height: 24 }, { fontSize: 12, color: C.muted }, "footer-title");
  addText(slide, `${n}/${total}`, { left: 1135, top: 674, width: 90, height: 24 }, { fontSize: 12, color: C.muted, alignment: "right" }, "footer-page");
}

function titleSlide(title, subtitle, n, total) {
  const slide = deck.slides.add();
  slide.background.fill = C.bg;
  addShape(slide, "left-band", "rect", { left: 0, top: 0, width: 18, height: H }, C.cyan);
  addText(slide, "SAVEFUNDS", { left: 72, top: 58, width: 240, height: 28 }, { fontSize: 14, bold: true, color: C.cyan }, "brand");
  addText(slide, title, { left: 72, top: 160, width: 830, height: 140 }, { fontSize: 46, bold: true, color: C.ink }, "title");
  addText(slide, subtitle, { left: 74, top: 320, width: 850, height: 80 }, { fontSize: 24, color: C.muted }, "subtitle");
  footer(slide, n, total);
  return slide;
}

function contentSlide(title, section, n, total) {
  const slide = deck.slides.add();
  slide.background.fill = C.bg;
  addShape(slide, "top-band", "rect", { left: 0, top: 0, width: W, height: 16 }, C.navy);
  addText(slide, section, { left: 72, top: 40, width: 330, height: 24 }, { fontSize: 13, bold: true, color: C.cyan }, "section");
  addText(slide, title, { left: 72, top: 72, width: 900, height: 58 }, { fontSize: 34, bold: true, color: C.ink }, "title");
  footer(slide, n, total);
  return slide;
}

function card(slide, left, top, width, height, title, body, color = C.cyan) {
  addShape(slide, "card", "roundRect", { left, top, width, height }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-lg", shadow: "shadow-sm" });
  addShape(slide, "card-accent", "rect", { left, top, width: 6, height }, color);
  addText(slide, title, { left: left + 22, top: top + 18, width: width - 42, height: 32 }, { fontSize: 20, bold: true, color: C.ink }, "card-title");
  addText(slide, body, { left: left + 22, top: top + 58, width: width - 42, height: height - 72 }, { fontSize: 17, color: C.muted }, "card-body");
}

function pill(slide, left, top, text, fill, width = 160) {
  addShape(slide, "pill", "roundRect", { left, top, width, height: 44 }, fill, { style: "solid", fill, fill, width: 1 }, { borderRadius: "rounded-full" });
  addText(slide, text, { left: left + 10, top: top + 11, width: width - 20, height: 22 }, { fontSize: 17, bold: true, color: C.white, alignment: "center" }, "pill-text");
}

const slides = [
  () => {
    const s = deck.slides.add();
    s.background.fill = C.navy;
    addShape(s, "accent", "rect", { left: 0, top: 0, width: 18, height: H }, C.cyan);
    addText(s, "SAVEFUNDS", { left: 72, top: 62, width: 260, height: 34 }, { fontSize: 16, bold: true, color: C.cyan }, "brand");
    addText(s, "Solution d'aide à la décision pour les retraits financiers en PME", { left: 72, top: 178, width: 900, height: 160 }, { fontSize: 48, bold: true, color: C.white }, "main-title");
    addText(s, "Défense TFE - application full-stack Spring Boot / Vue / PostgreSQL", { left: 76, top: 358, width: 820, height: 48 }, { fontSize: 24, color: "#C7D2FE" }, "subtitle");
    addText(s, "Steve Djomatchoua Monthe\nBachelier en informatique - HEPL\nAnnee academique 2025-2026", { left: 76, top: 575, width: 600, height: 78 }, { fontSize: 16, color: "#D7DEE9" }, "author");
  },
  () => {
    const s = contentSlide("Message directeur de la défense", "01 - Fil conducteur", 2, 18);
    addText(s, "SaveFunds transforme une question floue - puis-je retirer cet argent sans fragiliser ma société ? - en décision argumentée.", { left: 90, top: 170, width: 980, height: 76 }, { fontSize: 31, bold: true, color: C.ink }, "key-message");
    card(s, 96, 295, 315, 180, "Metier", "Aider le dirigeant de PME/SRL a anticiper le risque de trésorerie.", C.green);
    card(s, 482, 295, 315, 180, "Technique", "Structurer une API REST securisee autour d'une logique financière claire.", C.cyan);
    card(s, 868, 295, 315, 180, "Défense", "Montrer les calculs, le workflow et les choix de mise en production.", C.violet);
  },
  () => {
    const s = contentSlide("Contexte du stage et contribution", "02 - Introduction", 3, 18);
    card(s, 80, 165, 500, 220, "PROFACILE SRL", "Développement de solutions web et accompagnement numérique de clients avec des besoins métier spécifiques.", C.cyan);
    card(s, 700, 165, 430, 220, "Mon rôle", "Backend Spring Boot\nAPI REST\nLogique métier financière\nTests et validation", C.green);
    addText(s, "Projet mene avec deux stagiaires : separation backend / frontend, collaboration par Git et validation progressive du MVP.", { left: 100, top: 455, width: 1000, height: 65 }, { fontSize: 24, color: C.ink }, "collab");
  },
  () => {
    const s = contentSlide("Probleme métier identifie", "03 - Problématique", 4, 18);
    addText(s, "Un dirigeant peut effectuer un retrait personnel sans voir immédiatement l'impact sur la trésorerie, les charges futures et le compte courant.", { left: 80, top: 158, width: 960, height: 80 }, { fontSize: 27, bold: true, color: C.ink }, "problem");
    card(s, 90, 295, 300, 170, "Visibilité faible", "Les données sont souvent dispersées ou analysées trop tard.", C.amber);
    card(s, 490, 295, 300, 170, "Risque financier", "Un retrait peut réduire la réserve nécessaire aux charges.", C.red);
    card(s, 890, 295, 300, 170, "Besoin de décision", "Le dirigeant veut une réponse simple, mais justifiée.", C.cyan);
  },
  () => {
    const s = contentSlide("Solution proposee : SaveFunds", "04 - Solution", 5, 18);
    addText(s, "L'application produit une décision tricolore à partir de quatre indicateurs financiers.", { left: 84, top: 154, width: 900, height: 50 }, { fontSize: 27, bold: true, color: C.ink }, "solution");
    pill(s, 126, 260, "VERT", C.green);
    pill(s, 336, 260, "ORANGE", C.amber);
    pill(s, 570, 260, "ROUGE", C.red);
    addShape(s, "flow-line", "rect", { left: 162, top: 405, width: 820, height: 4 }, C.line);
    ["Données", "Calculs", "Décision", "Recommandations"].forEach((label, i) => {
      const x = 110 + i * 270;
      addShape(s, "flow-dot", "ellipse", { left: x, top: 377, width: 60, height: 60 }, i === 2 ? C.cyan : C.white, { style: "solid", fill: C.cyan, width: 2 });
      addText(s, String(i + 1), { left: x, top: 394, width: 60, height: 22 }, { fontSize: 20, bold: true, color: i === 2 ? C.white : C.cyan, alignment: "center" }, "flow-number");
      addText(s, label, { left: x - 50, top: 455, width: 160, height: 32 }, { fontSize: 18, bold: true, alignment: "center", color: C.ink }, "flow-label");
    });
  },
  () => {
    const s = contentSlide("Parcours utilisateur montre pendant la demo", "05 - Workflow", 6, 18);
    const items = [
      ["1", "Connexion", "Le dirigeant s'authentifie et obtient un token JWT."],
      ["2", "Entreprise", "Il encode les données financières de la société."],
      ["3", "Analyse", "Il indique le montant souhaité et lance le calcul."],
      ["4", "Résultat", "Il reçoit une couleur, des scores et des recommandations."],
    ];
    items.forEach(([n, t, b], i) => {
      const top = 155 + i * 105;
      addShape(s, "step-num", "ellipse", { left: 95, top, width: 58, height: 58 }, C.navy);
      addText(s, n, { left: 95, top: top + 15, width: 58, height: 24 }, { fontSize: 22, bold: true, color: C.white, alignment: "center" }, "step-n");
      addText(s, t, { left: 180, top: top + 3, width: 320, height: 30 }, { fontSize: 23, bold: true, color: C.ink }, "step-title");
      addText(s, b, { left: 180, top: top + 40, width: 760, height: 34 }, { fontSize: 18, color: C.muted }, "step-body");
    });
  },
  () => {
    const s = contentSlide("Données collectees pour l'analyse", "06 - Données", 7, 18);
    const rows = [
      ["Chiffre d'affaires mensuel", "Capacite d'activite moyenne"],
      ["Charges mensuelles", "Base du besoin de trésorerie"],
      ["Trésorerie disponible", "Liquidité en banque"],
      ["Solde du compte courant", "Risque debiteur dirigeant"],
      ["Montant souhaité", "Retrait demandé par le dirigeant"],
    ];
    rows.forEach(([a, b], i) => {
      const y = 155 + i * 72;
      addShape(s, "row", "roundRect", { left: 120, top: y, width: 1040, height: 54 }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-md" });
      addText(s, a, { left: 150, top: y + 14, width: 420, height: 24 }, { fontSize: 19, bold: true, color: C.ink }, "data-name");
      addText(s, b, { left: 610, top: y + 14, width: 480, height: 24 }, { fontSize: 18, color: C.muted }, "data-why");
    });
  },
  () => {
    const s = contentSlide("Les quatre indicateurs techniques", "07 - Calculs", 8, 18);
    card(s, 72, 160, 535, 145, "1. Couverture de trésorerie", "trésorerie / charges mensuelles", C.green);
    card(s, 672, 160, 535, 145, "2. Ratio CA / charges", "chiffre d'affaires mensuel / charges mensuelles", C.cyan);
    card(s, 72, 360, 535, 145, "3. Durée compte courant débiteur", "nombre de jours avec solde dirigeant négatif", C.amber);
    card(s, 672, 360, 535, 145, "4. Montant maximum prélevable", "trésorerie - reserve de sécurité de 3 mois", C.red);
  },
  () => {
    const s = contentSlide("Exemple de calcul explique au jury", "08 - Exemple chiffre", 9, 18);
    addText(s, "Données : CA 10 000 EUR - charges 2 000 EUR - trésorerie 7 500 EUR - retrait demande 2 000 EUR", { left: 76, top: 145, width: 1100, height: 36 }, { fontSize: 20, bold: true, color: C.ink }, "data");
    card(s, 82, 220, 340, 165, "Trésorerie", "7 500 / 2 000 = 3,75 mois\n=> VERT", C.green);
    card(s, 470, 220, 340, 165, "Ratio CA/charges", "10 000 / 2 000 = 5\n=> VERT", C.green);
    card(s, 858, 220, 340, 165, "Montant max", "7 500 - (2 000 x 3) = 1 500 EUR", C.amber);
    addText(s, "Le retrait demande est de 2 000 EUR alors que le maximum calculé est 1 500 EUR : le critère montant devient ROUGE.", { left: 110, top: 465, width: 1000, height: 64 }, { fontSize: 27, bold: true, color: C.red }, "result");
  },
  () => {
    const s = contentSlide("Grille tricolore et décision globale", "09 - Grille", 10, 18);
    const x = [80, 360, 590, 820, 1050];
    const headers = ["Critere", "VERT", "ORANGE", "ROUGE", "Sens"];
    headers.forEach((h, i) => addText(s, h, { left: x[i], top: 155, width: i === 0 ? 240 : 190, height: 26 }, { fontSize: 18, bold: true, color: i === 1 ? C.green : i === 2 ? C.amber : i === 3 ? C.red : C.ink }, "head"));
    const rows = [
      ["Trésorerie", ">= 3 mois", "1 à 3 mois", "< 1 mois", "Réserve"],
      ["CA / charges", ">= 1,3", "1,0 a 1,3", "< 1,0", "Rentabilite"],
      ["Compte courant", "0 jour", "1 a 30 j", "> 30 j", "Risque"],
      ["Montant", "<= 75 % max", "75-100 %", "> max", "Liquidité"],
    ];
    rows.forEach((r, ri) => {
      const y = 208 + ri * 70;
      addShape(s, "row-bg", "roundRect", { left: 60, top: y - 8, width: 1150, height: 54 }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-md" });
      r.forEach((txt, ci) => addText(s, txt, { left: x[ci], top: y + 6, width: ci === 0 ? 240 : 190, height: 24 }, { fontSize: 17, color: ci === 1 ? C.green : ci === 2 ? C.amber : ci === 3 ? C.red : C.ink, bold: ci === 0 }, "cell"));
    });
    addText(s, "Règle simple : si un seul indicateur est rouge, la décision globale est rouge.", { left: 92, top: 545, width: 1030, height: 40 }, { fontSize: 24, bold: true, color: C.ink }, "weakest");
  },
  () => {
    const s = contentSlide("Architecture technique", "10 - Backend", 11, 18);
    const labels = ["Controller", "DTO / Mapper", "Service métier", "Repository", "PostgreSQL"];
    labels.forEach((label, i) => {
      const left = 80 + i * 230;
      addShape(s, "arch-box", "roundRect", { left, top: 250, width: 175, height: 90 }, i === 2 ? C.navy : C.white, { style: "solid", fill: i === 2 ? C.navy : C.line, width: 1 }, { borderRadius: "rounded-lg" });
      addText(s, label, { left: left + 12, top: 278, width: 150, height: 30 }, { fontSize: 18, bold: true, color: i === 2 ? C.white : C.ink, alignment: "center" }, "arch-label");
      if (i < labels.length - 1) addText(s, ">", { left: left + 185, top: 277, width: 40, height: 30 }, { fontSize: 32, bold: true, color: C.cyan, alignment: "center" }, "arrow");
    });
    addText(s, "Le service concentre la logique financière : il évite de disperser les calculs dans les contrôleurs ou dans le frontend.", { left: 115, top: 430, width: 1010, height: 58 }, { fontSize: 25, bold: true, color: C.ink }, "architecture-note");
  },
  () => {
    const s = contentSlide("Securite et separation des responsabilites", "11 - Securite", 12, 18);
    card(s, 82, 165, 340, 180, "JWT", "Authentification sans session serveur et protection des endpoints.", C.cyan);
    card(s, 470, 165, 340, 180, "Ownership", "Un utilisateur ne peut pas manipuler les entreprises ou analyses d'un autre compte.", C.green);
    card(s, 858, 165, 340, 180, "Validation", "DTO, services et tests limitent les erreurs dans les flux critiques.", C.violet);
    addText(s, "Point important pour la défense : la sécurité n'est pas seulement l'écran de login, c'est aussi le contrôle d'accès côté backend.", { left: 100, top: 450, width: 1000, height: 66 }, { fontSize: 25, bold: true, color: C.ink }, "security-note");
  },
  () => {
    const s = contentSlide("Base de données et préparation production", "12 - Production", 13, 18);
    card(s, 92, 170, 315, 200, "PostgreSQL", "Base relationnelle pour conserver utilisateurs, entreprises, analyses et resultats.", C.cyan);
    card(s, 482, 170, 315, 200, "Flyway", "Le schema est versionne par migrations SQL rejouables.", C.green);
    card(s, 872, 170, 315, 200, "CI / Docker", "Tests automatises et environnement local reproductible.", C.violet);
    addText(s, "Je ne détaille pas toute la mise en production : je montre surtout que l'application est préparée pour être configurée hors du code.", { left: 100, top: 460, width: 1000, height: 58 }, { fontSize: 24, bold: true, color: C.ink }, "prod-note");
  },
  () => {
    const s = contentSlide("Demo fonctionnelle conseillee", "13 - Demo", 14, 18);
    const steps = [
      "1. Connexion utilisateur",
      "2. Création ou sélection d'une entreprise",
      "3. Encodage des données financières",
      "4. Demande de retrait",
      "5. Lecture de la décision et des recommandations",
    ];
    steps.forEach((txt, i) => {
      addShape(s, "demo-step", "roundRect", { left: 155, top: 150 + i * 75, width: 880, height: 52 }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-md" });
      addText(s, txt, { left: 190, top: 164 + i * 75, width: 760, height: 24 }, { fontSize: 20, bold: true, color: C.ink }, "demo-text");
    });
  },
  () => {
    const s = contentSlide("Trois scenarios a tester devant le jury", "14 - Tests demo", 15, 18);
    card(s, 82, 165, 340, 215, "Scénario vert", "CA 50 000\nCharges 30 000\nTrésorerie 120 000\nRetrait 10 000", C.green);
    card(s, 470, 165, 340, 215, "Scénario orange", "CA 36 000\nCharges 30 000\nTrésorerie 60 000\nRetrait 5 000", C.amber);
    card(s, 858, 165, 340, 215, "Scénario rouge", "CA 25 000\nCharges 30 000\nTrésorerie 20 000\nRetrait 5 000", C.red);
    addText(s, "Ces trois cas montrent que le systeme ne donne pas toujours la meme couleur : il reagit aux montants et aux ratios.", { left: 100, top: 470, width: 1000, height: 56 }, { fontSize: 24, bold: true, color: C.ink }, "test-note");
  },
  () => {
    const s = contentSlide("Qualite logicielle", "15 - Validation", 16, 18);
    card(s, 90, 165, 315, 180, "Tests backend", "Tests unitaires et integration Spring Boot sur les services et contrôleurs.", C.green);
    card(s, 482, 165, 315, 180, "Build frontend", "Verification TypeScript et build Vite avant livraison.", C.cyan);
    card(s, 872, 165, 315, 180, "GitHub Actions", "CI séparée pour backend et frontend dans leurs propres repos.", C.violet);
    addText(s, "Objectif : detecter les regressions avant une demonstration ou une mise en production.", { left: 120, top: 450, width: 940, height: 48 }, { fontSize: 26, bold: true, color: C.ink }, "quality-note");
  },
  () => {
    const s = contentSlide("Limites actuelles et évolutions", "16 - Perspectives", 17, 18);
    card(s, 100, 165, 460, 275, "Limites du MVP", "Saisie manuelle des données\nSeuils identiques pour toutes les entreprises\nAnalyse basée sur une situation ponctuelle", C.amber);
    card(s, 720, 165, 460, 275, "Évolutions possibles", "Connexion bancaire ou comptable\nHistorique détaille des analyses\nAlertes automatiques\nExports PDF", C.green);
  },
  () => {
    const s = deck.slides.add();
    s.background.fill = C.navy;
    addShape(s, "accent", "rect", { left: 0, top: 0, width: 18, height: H }, C.green);
    addText(s, "Conclusion", { left: 80, top: 86, width: 720, height: 60 }, { fontSize: 42, bold: true, color: C.white }, "title");
    addText(s, "SaveFunds est un MVP fonctionnel qui relie un besoin métier concret, une logique financière explicable et une architecture backend structurée.", { left: 82, top: 185, width: 980, height: 96 }, { fontSize: 30, bold: true, color: "#E5EEF9" }, "conclusion");
    addText(s, "Questions ?", { left: 82, top: 420, width: 500, height: 70 }, { fontSize: 48, bold: true, color: C.green }, "questions");
    addText(s, "Merci pour votre attention", { left: 86, top: 500, width: 520, height: 38 }, { fontSize: 22, color: "#C7D2FE" }, "thanks");
  },
];

slides.forEach((fn) => fn());

const sourceNotes = [
  "Deck cree pour une défense TFE SaveFunds de 15 a 20 minutes.",
  "Contenu base sur les informations fournies par l'utilisateur et l'audit local du projet.",
  "Technologies: Spring Boot 3.4, Java 21, PostgreSQL, Vue 3/Vite, JWT, Flyway, Docker, GitHub Actions.",
  "Les valeurs des scenarios de demo sont des jeux de test pedagogiques proposes pendant la préparation.",
].join("\n");
await fs.writeFile(path.join(qaDir, "source-notes.txt"), sourceNotes, "utf8");

for (const [index, slide] of deck.slides.items.entries()) {
  const stem = `slide-${String(index + 1).padStart(2, "0")}`;
  const png = await deck.export({ slide, format: "png", scale: 1 });
  await fs.writeFile(path.join(qaDir, `${stem}.png`), new Uint8Array(await png.arrayBuffer()));
  const layout = await slide.export({ format: "layout" });
  await fs.writeFile(path.join(qaDir, `${stem}.layout.json`), await layout.text(), "utf8");
}

const montage = await deck.export({ format: "png", montage: true, scale: 0.45 });
await fs.writeFile(path.join(qaDir, "montage.png"), new Uint8Array(await montage.arrayBuffer()));

const pptx = await PresentationFile.exportPptx(deck);
await pptx.save(finalPptx);

const stat = await fs.stat(finalPptx);
console.log(JSON.stringify({ finalPptx, slideCount: deck.slides.items.length, bytes: stat.size, montage: path.join(qaDir, "montage.png") }, null, 2));
