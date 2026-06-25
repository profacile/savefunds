import fs from "node:fs/promises";
import path from "node:path";
import { Presentation, PresentationFile } from "file:///C:/Users/SHH8701/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/@oai/artifact-tool/dist/artifact_tool.mjs";

const root = "C:\\Users\\SHH8701\\opt\\personnal\\PROFACILE_SRL\\savefunds";
const outDir = path.join(root, "output", "pptx");
const qaDir = path.join(root, "output", "pptx", "qa-savefunds-defense-expert");
await fs.mkdir(outDir, { recursive: true });
await fs.mkdir(qaDir, { recursive: true });

const finalPptx = path.join(outDir, "SaveFunds_Defense_15_20min_Expert.pptx");
const W = 1280;
const H = 720;
const TOTAL = 19;

const C = {
  navy: "#071426",
  ink: "#172033",
  muted: "#64748B",
  bg: "#F6F8FB",
  white: "#FFFFFF",
  line: "#D8E0EA",
  cyan: "#0EA5E9",
  green: "#16A34A",
  amber: "#F59E0B",
  red: "#DC2626",
  violet: "#7C3AED",
};

const deck = Presentation.create({ slideSize: { width: W, height: H } });
let page = 0;

function shape(slide, name, geometry, position, fill, line = { style: "solid", fill: "none", width: 0 }, extra = {}) {
  return slide.shapes.add({ name, geometry, position, fill, line, ...extra });
}

function text(slide, value, position, style = {}, name = "text") {
  const box = shape(slide, name, "textbox", position, "none");
  box.text = value;
  box.text.style = {
    typeface: "Aptos",
    fontSize: 21,
    color: C.ink,
    ...style,
  };
  return box;
}

function footer(slide) {
  shape(slide, "footer-line", "rect", { left: 56, top: 660, width: 1168, height: 1 }, C.line);
  text(slide, "SaveFunds - Défense TFE", { left: 56, top: 674, width: 420, height: 24 }, { fontSize: 12, color: C.muted }, "footer-title");
  text(slide, `${page}/${TOTAL}`, { left: 1135, top: 674, width: 90, height: 24 }, { fontSize: 12, color: C.muted, alignment: "right" }, "footer-page");
}

function slide(title, section) {
  page += 1;
  const s = deck.slides.add();
  s.background.fill = C.bg;
  shape(s, "top-band", "rect", { left: 0, top: 0, width: W, height: 16 }, C.navy);
  text(s, section, { left: 72, top: 40, width: 370, height: 24 }, { fontSize: 13, bold: true, color: C.cyan }, "section");
  text(s, title, { left: 72, top: 72, width: 930, height: 58 }, { fontSize: 34, bold: true, color: C.ink }, "title");
  footer(s);
  return s;
}

function cover() {
  page += 1;
  const s = deck.slides.add();
  s.background.fill = C.navy;
  shape(s, "accent", "rect", { left: 0, top: 0, width: 18, height: H }, C.cyan);
  text(s, "SAVEFUNDS", { left: 72, top: 62, width: 260, height: 34 }, { fontSize: 16, bold: true, color: C.cyan }, "brand");
  text(s, "Vigilance financière pour les retraits en PME et SRL", { left: 72, top: 170, width: 930, height: 145 }, { fontSize: 49, bold: true, color: C.white }, "main-title");
  text(s, "Application full-stack : Spring Boot 3.4 / Java 21 / PostgreSQL / Vue", { left: 76, top: 340, width: 920, height: 48 }, { fontSize: 24, color: "#C7D2FE" }, "subtitle");
  text(s, "Steve Djomatchoua Monthe\nBachelier en informatique - HEPL\nAnnée académique 2025-2026", { left: 76, top: 575, width: 620, height: 78 }, { fontSize: 16, color: "#D7DEE9" }, "author");
}

function card(s, left, top, width, height, title, body, color = C.cyan) {
  shape(s, "card", "roundRect", { left, top, width, height }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-lg", shadow: "shadow-sm" });
  shape(s, "accent", "rect", { left, top, width: 6, height }, color);
  text(s, title, { left: left + 22, top: top + 18, width: width - 44, height: 32 }, { fontSize: 20, bold: true, color: C.ink }, "card-title");
  text(s, body, { left: left + 22, top: top + 58, width: width - 44, height: height - 72 }, { fontSize: 17, color: C.muted }, "card-body");
}

function pill(s, left, top, value, fill, width = 168) {
  shape(s, "pill", "roundRect", { left, top, width, height: 44 }, fill, { style: "solid", fill, width: 1 }, { borderRadius: "rounded-full" });
  text(s, value, { left: left + 10, top: top + 11, width: width - 20, height: 22 }, { fontSize: 17, bold: true, color: C.white, alignment: "center" }, "pill-text");
}

cover();

{
  const s = slide("Le message que je veux faire passer", "01 - Fil conducteur");
  text(s, "SaveFunds ne remplace pas l'expert-comptable : il donne au dirigeant une première lecture structurée, traçable et compréhensible avant un retrait personnel.", { left: 88, top: 165, width: 1040, height: 92 }, { fontSize: 30, bold: true, color: C.ink }, "message");
  card(s, 96, 315, 315, 170, "Décision rapide", "Une couleur globale : vert, orange ou rouge.", C.green);
  card(s, 482, 315, 315, 170, "Justification", "Chaque couleur vient d'indicateurs et de seuils explicites.", C.cyan);
  card(s, 868, 315, 315, 170, "Préparation prod", "Configuration externe, migrations DB, tests et CI.", C.violet);
}

{
  const s = slide("Contexte : un besoin concret de dirigeant", "02 - Contexte");
  card(s, 84, 160, 330, 180, "Client cible", "Dirigeants de PME belges et SRL qui veulent prélever prudemment.", C.cyan);
  card(s, 474, 160, 330, 180, "Acteur associé", "Comptable ou conseiller qui valide les flux et interprète les recommandations.", C.green);
  card(s, 864, 160, 330, 180, "Risque", "Confondre argent disponible et argent réellement prélevable.", C.red);
  text(s, "Mon rôle principal : structurer le backend, l'API REST, la logique financière, la sécurité et les tests.", { left: 108, top: 430, width: 1000, height: 60 }, { fontSize: 27, bold: true, color: C.ink }, "role");
}

{
  const s = slide("Problématique métier", "03 - Problème");
  text(s, "Un retrait peut sembler possible parce que le compte bancaire est positif, mais devenir dangereux si les charges, le chiffre d'affaires ou le compte courant sont fragiles.", { left: 82, top: 158, width: 1030, height: 88 }, { fontSize: 29, bold: true, color: C.ink }, "problem");
  card(s, 92, 310, 310, 160, "Court terme", "Risque de manquer de trésorerie pour payer les charges.", C.red);
  card(s, 486, 310, 310, 160, "Lecture comptable", "Besoin de relier les chiffres à des indicateurs lisibles.", C.amber);
  card(s, 880, 310, 310, 160, "Traçabilité", "Expliquer pourquoi le retrait est conseillé ou déconseillé.", C.cyan);
}

{
  const s = slide("La réponse SaveFunds", "04 - Solution");
  text(s, "L'utilisateur encode les données financières clés, demande un montant de retrait, puis SaveFunds calcule quatre indicateurs et produit une décision tricolore.", { left: 84, top: 150, width: 1040, height: 70 }, { fontSize: 26, bold: true, color: C.ink }, "solution");
  pill(s, 156, 280, "VERT", C.green);
  pill(s, 383, 280, "ORANGE", C.amber);
  pill(s, 645, 280, "ROUGE", C.red);
  card(s, 100, 400, 250, 125, "Données", "CA, charges, trésorerie, compte courant.", C.cyan);
  card(s, 390, 400, 250, 125, "Calculs", "Ratios et seuils métier.", C.violet);
  card(s, 680, 400, 250, 125, "Décision", "Couleur globale + détails.", C.green);
  card(s, 970, 400, 220, 125, "Conseil", "Recommandations contextualisées.", C.amber);
}

{
  const s = slide("Références utilisées dans la logique métier", "05 - Référentiel");
  card(s, 80, 155, 535, 145, "Trésorerie", "Couverture en mois de charges : logique de réserve de sécurité inspirée des pratiques BNB.", C.green);
  card(s, 670, 155, 535, 145, "Rentabilité opérationnelle", "Ratio chiffre d'affaires / charges pour détecter une activité fragile.", C.cyan);
  card(s, 80, 360, 535, 145, "Compte courant débiteur", "Durée de situation négative : point de vigilance fiscal et comptable.", C.amber);
  card(s, 670, 360, 535, 145, "Montant prélevable", "Retrait comparé au maximum disponible après conservation d'une réserve.", C.red);
}

{
  const s = slide("Workflow dirigeant et comptable", "06 - Workflow");
  const steps = [
    ["1", "Dirigeant", "Encode les données et demande un retrait."],
    ["2", "Backend", "Contrôle l'accès, calcule les indicateurs et persiste le résultat."],
    ["3", "Comptable", "Consulte ou valide les flux selon le dossier client."],
    ["4", "Décision", "Le retrait est accepté, surveillé ou déconseillé."],
  ];
  steps.forEach(([n, title, body], i) => {
    const top = 155 + i * 105;
    shape(s, "num", "ellipse", { left: 95, top, width: 58, height: 58 }, C.navy);
    text(s, n, { left: 95, top: top + 15, width: 58, height: 24 }, { fontSize: 22, bold: true, color: C.white, alignment: "center" }, "num-text");
    text(s, title, { left: 180, top: top + 2, width: 330, height: 30 }, { fontSize: 23, bold: true, color: C.ink }, "step-title");
    text(s, body, { left: 180, top: top + 40, width: 820, height: 34 }, { fontSize: 18, color: C.muted }, "step-body");
  });
}

{
  const s = slide("Données nécessaires au calcul", "07 - Données");
  const rows = [
    ["Chiffre d'affaires mensuel", "Volume d'activité moyen"],
    ["Charges mensuelles", "Besoin minimal pour survivre plusieurs mois"],
    ["Trésorerie disponible", "Liquidité réellement présente"],
    ["Solde compte courant", "Situation dirigeant créditrice ou débitrice"],
    ["Montant souhaité", "Retrait demandé à comparer au maximum autorisé"],
  ];
  rows.forEach(([a, b], i) => {
    const y = 152 + i * 74;
    shape(s, "row", "roundRect", { left: 110, top: y, width: 1060, height: 56 }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-md" });
    text(s, a, { left: 142, top: y + 15, width: 420, height: 24 }, { fontSize: 19, bold: true, color: C.ink }, "data");
    text(s, b, { left: 590, top: y + 15, width: 520, height: 24 }, { fontSize: 18, color: C.muted }, "why");
  });
}

{
  const s = slide("Les quatre calculs techniques", "08 - Calculs");
  card(s, 72, 158, 535, 145, "1. Couverture de trésorerie", "trésorerie / charges mensuelles\nObjectif : mesurer la réserve en mois.", C.green);
  card(s, 672, 158, 535, 145, "2. Ratio CA / charges", "CA mensuel / charges mensuelles\nObjectif : mesurer la marge d'exploitation.", C.cyan);
  card(s, 72, 360, 535, 145, "3. Durée compte courant débiteur", "nombre de jours avec solde négatif\nObjectif : détecter une situation durable.", C.amber);
  card(s, 672, 360, 535, 145, "4. Maximum prélevable", "trésorerie - 3 mois de charges\nObjectif : préserver une réserve.", C.red);
}

{
  const s = slide("Exemple chiffré à expliquer oralement", "09 - Exemple");
  text(s, "Données : CA 10 000 EUR - charges 2 000 EUR - trésorerie 7 500 EUR - retrait demandé 2 000 EUR", { left: 76, top: 145, width: 1100, height: 36 }, { fontSize: 20, bold: true, color: C.ink }, "data");
  card(s, 82, 220, 340, 165, "Trésorerie", "7 500 / 2 000 = 3,75 mois\n=> VERT", C.green);
  card(s, 470, 220, 340, 165, "Ratio CA/charges", "10 000 / 2 000 = 5\n=> VERT", C.green);
  card(s, 858, 220, 340, 165, "Montant max", "7 500 - (2 000 x 3) = 1 500 EUR", C.amber);
  text(s, "Le retrait demandé dépasse le maximum calculé : la décision globale devient ROUGE malgré deux indicateurs verts.", { left: 110, top: 465, width: 1000, height: 64 }, { fontSize: 27, bold: true, color: C.red }, "result");
}

{
  const s = slide("Grille tricolore et règle de décision", "10 - Grille");
  const x = [80, 360, 590, 820, 1050];
  ["Critère", "VERT", "ORANGE", "ROUGE", "Sens"].forEach((h, i) => text(s, h, { left: x[i], top: 155, width: i === 0 ? 240 : 190, height: 26 }, { fontSize: 18, bold: true, color: i === 1 ? C.green : i === 2 ? C.amber : i === 3 ? C.red : C.ink }, "head"));
  const rows = [
    ["Trésorerie", ">= 3 mois", "1 à 3 mois", "< 1 mois", "Réserve"],
    ["CA / charges", ">= 1,3", "1,0 à 1,3", "< 1,0", "Rentabilité"],
    ["Compte courant", "0 jour", "1 à 30 j", "> 30 j", "Risque"],
    ["Montant", "<= 75 % max", "75-100 %", "> max", "Liquidité"],
  ];
  rows.forEach((r, ri) => {
    const y = 208 + ri * 70;
    shape(s, "row", "roundRect", { left: 60, top: y - 8, width: 1150, height: 54 }, C.white, { style: "solid", fill: C.line, width: 1 }, { borderRadius: "rounded-md" });
    r.forEach((v, ci) => text(s, v, { left: x[ci], top: y + 6, width: ci === 0 ? 240 : 190, height: 24 }, { fontSize: 17, bold: ci === 0, color: ci === 1 ? C.green : ci === 2 ? C.amber : ci === 3 ? C.red : C.ink }, "cell"));
  });
  text(s, "Principe : la décision globale suit le maillon faible. Un indicateur rouge suffit pour classer l'analyse en rouge.", { left: 92, top: 545, width: 1050, height: 40 }, { fontSize: 23, bold: true, color: C.ink }, "rule");
}

{
  const s = slide("Architecture backend", "11 - Architecture");
  const labels = ["Controller", "DTO / Mapper", "Service métier", "Repository", "PostgreSQL"];
  labels.forEach((label, i) => {
    const left = 80 + i * 230;
    shape(s, "box", "roundRect", { left, top: 250, width: 175, height: 90 }, i === 2 ? C.navy : C.white, { style: "solid", fill: i === 2 ? C.navy : C.line, width: 1 }, { borderRadius: "rounded-lg" });
    text(s, label, { left: left + 12, top: 278, width: 150, height: 30 }, { fontSize: 18, bold: true, color: i === 2 ? C.white : C.ink, alignment: "center" }, "label");
    if (i < labels.length - 1) text(s, ">", { left: left + 185, top: 277, width: 40, height: 30 }, { fontSize: 32, bold: true, color: C.cyan, alignment: "center" }, "arrow");
  });
  text(s, "Le service métier concentre les calculs et les règles de décision. Le frontend affiche le résultat, mais ne porte pas la logique financière critique.", { left: 118, top: 425, width: 1010, height: 68 }, { fontSize: 24, bold: true, color: C.ink }, "note");
}

{
  const s = slide("Sécurité et cohérence des données", "12 - Sécurité");
  card(s, 82, 165, 340, 180, "JWT", "Authentification sans session serveur et protection des endpoints.", C.cyan);
  card(s, 470, 165, 340, 180, "Ownership", "Le backend vérifie que l'utilisateur possède l'entreprise ou l'analyse demandée.", C.green);
  card(s, 858, 165, 340, 180, "Validation", "DTO, services et tests protègent les flux métier critiques.", C.violet);
  text(s, "Point à dire au jury : la sécurité n'est pas seulement l'écran de connexion, c'est aussi la vérification des droits dans chaque workflow sensible.", { left: 95, top: 445, width: 1040, height: 70 }, { fontSize: 25, bold: true, color: C.ink }, "note");
}

{
  const s = slide("Préparation à la production", "13 - Production");
  card(s, 92, 170, 315, 200, "PostgreSQL", "Persistance relationnelle des utilisateurs, entreprises, analyses et résultats.", C.cyan);
  card(s, 482, 170, 315, 200, "Flyway", "Schéma versionné par migrations SQL pour éviter les bases non maîtrisées.", C.green);
  card(s, 872, 170, 315, 200, "CI / Docker", "Base locale reproductible et vérifications automatiques sur GitHub.", C.violet);
  text(s, "Les secrets et paramètres sensibles sont externalisés : port, URL DB, mot de passe, JWT secret et CORS ne sont pas codés en dur.", { left: 100, top: 460, width: 1000, height: 60 }, { fontSize: 24, bold: true, color: C.ink }, "note");
}

{
  const s = slide("Démo : scénario de bout en bout", "14 - Démo");
  const steps = ["Connexion", "Création entreprise", "Saisie des chiffres", "Demande de retrait", "Résultat tricolore", "Recommandations"];
  steps.forEach((step, i) => {
    const left = 95 + (i % 3) * 370;
    const top = 165 + Math.floor(i / 3) * 170;
    card(s, left, top, 300, 120, `${i + 1}. ${step}`, i === 4 ? "Je montre la couleur et les scores." : "Étape visible dans l'écran.", i === 4 ? C.green : C.cyan);
  });
}

{
  const s = slide("Jeux de test à présenter", "15 - Tests");
  card(s, 82, 165, 340, 215, "Vert", "CA 50 000\nCharges 30 000\nTrésorerie 120 000\nRetrait 10 000", C.green);
  card(s, 470, 165, 340, 215, "Orange", "CA 36 000\nCharges 30 000\nTrésorerie 60 000\nRetrait 5 000", C.amber);
  card(s, 858, 165, 340, 215, "Rouge", "CA 25 000\nCharges 30 000\nTrésorerie 20 000\nRetrait 5 000", C.red);
  text(s, "Ces tests prouvent que le workflow réagit aux données et que la grille n'est pas décorative.", { left: 118, top: 470, width: 950, height: 50 }, { fontSize: 25, bold: true, color: C.ink }, "note");
}

{
  const s = slide("Qualité logicielle", "16 - Qualité");
  card(s, 90, 165, 315, 180, "Tests backend", "Tests Spring Boot sur services, contrôleurs et règles métier.", C.green);
  card(s, 482, 165, 315, 180, "Build frontend", "Type-check et build Vite avant livraison.", C.cyan);
  card(s, 872, 165, 315, 180, "Deux repos Git", "CI séparée : backend et frontend évoluent indépendamment.", C.violet);
  text(s, "Ce point montre que le projet n'est pas seulement une démo : il commence à intégrer des pratiques de livraison.", { left: 120, top: 450, width: 940, height: 58 }, { fontSize: 25, bold: true, color: C.ink }, "note");
}

{
  const s = slide("Limites et perspectives", "17 - Suite");
  card(s, 100, 165, 460, 275, "Limites du MVP", "Saisie manuelle des données\nSeuils identiques pour tous les profils\nAnalyse à un instant donné\nPas encore de connexion comptable", C.amber);
  card(s, 720, 165, 460, 275, "Évolutions réalistes", "Import bancaire ou comptable\nHistorique des analyses\nExports PDF\nAlertes automatiques\nParamétrage par secteur", C.green);
}

{
  page += 1;
  const s = deck.slides.add();
  s.background.fill = C.navy;
  shape(s, "accent", "rect", { left: 0, top: 0, width: 18, height: H }, C.green);
  text(s, "Conclusion", { left: 80, top: 86, width: 720, height: 60 }, { fontSize: 42, bold: true, color: C.white }, "title");
  text(s, "SaveFunds est un MVP fonctionnel qui transforme des données financières simples en décision argumentée, avec une architecture backend claire et une trajectoire crédible vers la production.", { left: 82, top: 185, width: 1030, height: 110 }, { fontSize: 30, bold: true, color: "#E5EEF9" }, "conclusion");
  text(s, "Questions ?", { left: 82, top: 430, width: 500, height: 70 }, { fontSize: 48, bold: true, color: C.green }, "questions");
  text(s, "Merci pour votre attention", { left: 86, top: 510, width: 520, height: 38 }, { fontSize: 22, color: "#C7D2FE" }, "thanks");
}

const sourceNotes = [
  "Deck expert créé pour une défense TFE SaveFunds de 15 à 20 minutes.",
  "Contenu basé sur le projet local SaveFunds, les informations utilisateur et les corrections réalisées dans le backend/frontend.",
  "Le frontend du projet local est Vue/Vite, même si certains documents initiaux mentionnaient Angular.",
  "Les références BNB/IRE/CIR92/CSA sont présentées comme cadre métier de vigilance, pas comme consultation juridique exhaustive.",
].join("\n");
await fs.writeFile(path.join(qaDir, "source-notes.txt"), sourceNotes, "utf8");

for (const [index, s] of deck.slides.items.entries()) {
  const stem = `slide-${String(index + 1).padStart(2, "0")}`;
  const png = await deck.export({ slide: s, format: "png", scale: 1 });
  await fs.writeFile(path.join(qaDir, `${stem}.png`), new Uint8Array(await png.arrayBuffer()));
  const layout = await s.export({ format: "layout" });
  await fs.writeFile(path.join(qaDir, `${stem}.layout.json`), await layout.text(), "utf8");
}

const montage = await deck.export({ format: "png", montage: true, scale: 0.45 });
await fs.writeFile(path.join(qaDir, "montage.png"), new Uint8Array(await montage.arrayBuffer()));

const pptx = await PresentationFile.exportPptx(deck);
await pptx.save(finalPptx);
const stat = await fs.stat(finalPptx);
console.log(JSON.stringify({ finalPptx, slideCount: deck.slides.items.length, bytes: stat.size, montage: path.join(qaDir, "montage.png") }, null, 2));
