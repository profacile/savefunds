import fs from "node:fs/promises";
import path from "node:path";
import os from "node:os";
import { FileBlob, PresentationFile } from "file:///C:/Users/SHH8701/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/@oai/artifact-tool/dist/artifact_tool.mjs";

const pptxPath =
  "C:\\Users\\SHH8701\\Downloads\\SaveFunds_TFE_Presentation_Revue.pptx  -  Réparation.pptx";
const workspace = path.join(os.tmpdir(), "codex-presentations-review-savefunds");
await fs.mkdir(workspace, { recursive: true });

const presentation = await PresentationFile.importPptx(
  await FileBlob.load(pptxPath),
);

const snapshot = await presentation.inspect({
  kind: "slide,textbox,shape,image,table,chart,notes,layout",
  maxChars: 120000,
});

await fs.writeFile(path.join(workspace, "inspect.ndjson"), snapshot.ndjson, "utf8");

let montagePath = null;
try {
  const montage = await presentation.export({
    format: "png",
    montage: true,
    scale: 0.55,
  });
  montagePath = path.join(workspace, "montage.png");
  await fs.writeFile(montagePath, new Uint8Array(await montage.arrayBuffer()));
} catch (error) {
  await fs.writeFile(
    path.join(workspace, "render-error.txt"),
    error?.stack || String(error),
    "utf8",
  );
}

const lines = snapshot.ndjson.split(/\r?\n/).filter(Boolean);
const slideLines = lines.filter((line) => line.includes('"kind":"slide"'));
const textLines = lines.filter((line) => line.includes('"kind":"textbox"'));

console.log(
  JSON.stringify(
    {
      slideCount: presentation.slides.items.length,
      inspectPath: path.join(workspace, "inspect.ndjson"),
      montagePath,
      slideLineCount: slideLines.length,
      textboxLineCount: textLines.length,
    },
    null,
    2,
  ),
);
