/// <reference types="node" />

import { promises as fs } from "fs";
import path from "path";
import QRCode from "qrcode";

import { roomInfoBySvgId } from "../src/data/hospitalRoomInfo";
import {
  buildRoomQrCode,
  QR_ANCHOR_REGISTRY,
  type QrAnchor,
} from "../src/data/hospitalRouteGraph";

type IncludeMode = "rooms" | "anchors" | "all";

type CliOptions = {
  outDir: string;
  include: IncludeMode;
  size: number;
  margin: number;
  clean: boolean;
};

type GeneratedQr = {
  type: "room" | "anchor";
  id: string;
  label: string;
  payload: string;
  fileName: string;
  relativePath: string;
};

const DEFAULT_OPTIONS: CliOptions = {
  outDir: "public/images/qr",
  include: "all",
  size: 512,
  margin: 2,
  clean: false,
};

const normalizeInclude = (value: string | undefined): IncludeMode => {
  if (!value) return "all";
  if (value === "rooms" || value === "anchors" || value === "all") return value;
  throw new Error(`Invalid --include value: "${value}". Use rooms|anchors|all.`);
};

const parsePositiveInt = (value: string | undefined, fallback: number, name: string): number => {
  if (!value) return fallback;
  const parsed = Number.parseInt(value, 10);
  if (!Number.isFinite(parsed) || parsed <= 0) {
    throw new Error(`Invalid --${name} value: "${value}". Must be a positive integer.`);
  }
  return parsed;
};

const parseCliArgs = (): CliOptions => {
  const args = process.argv.slice(2);
  const options: CliOptions = { ...DEFAULT_OPTIONS };

  for (let i = 0; i < args.length; i += 1) {
    const arg = args[i];

    if (arg === "--clean") {
      options.clean = true;
      continue;
    }

    if (arg === "--outDir") {
      options.outDir = args[i + 1] || options.outDir;
      i += 1;
      continue;
    }

    if (arg === "--include") {
      options.include = normalizeInclude(args[i + 1]);
      i += 1;
      continue;
    }

    if (arg === "--size") {
      options.size = parsePositiveInt(args[i + 1], options.size, "size");
      i += 1;
      continue;
    }

    if (arg === "--margin") {
      options.margin = parsePositiveInt(args[i + 1], options.margin, "margin");
      i += 1;
      continue;
    }

    if (arg === "--help" || arg === "-h") {
      printHelp();
      process.exit(0);
    }

    throw new Error(`Unknown argument: ${arg}`);
  }

  return options;
};

const printHelp = (): void => {
  console.log(`\nQR Generator\n\nUsage:\n  npm run qr:generate -- [options]\n\nOptions:\n  --include <rooms|anchors|all>  Which QR sets to generate (default: all)\n  --outDir <path>                 Output directory (default: public/images/qr)\n  --size <number>                 PNG width in px (default: 512)\n  --margin <number>               QR margin (default: 2)\n  --clean                         Remove existing PNG files in output directory\n  --help                          Show this help\n`);
};

const sanitizeFileName = (value: string): string => {
  return value
    .replace(/[^a-zA-Z0-9._-]/g, "_")
    .replace(/_+/g, "_")
    .replace(/^_+|_+$/g, "");
};

const ensureDir = async (dirPath: string): Promise<void> => {
  await fs.mkdir(dirPath, { recursive: true });
};

const cleanPngFiles = async (dirPath: string): Promise<number> => {
  let removed = 0;
  const entries = await fs.readdir(dirPath, { withFileTypes: true }).catch(() => [] as any[]);

  for (const entry of entries) {
    if (!entry.isFile()) continue;
    if (!entry.name.toLowerCase().endsWith(".png")) continue;
    await fs.unlink(path.join(dirPath, entry.name));
    removed += 1;
  }

  return removed;
};

const roomQrItems = (): GeneratedQr[] => {
  return Object.values(roomInfoBySvgId)
    .sort((a, b) => a.name.localeCompare(b.name))
    .map((room) => {
      const payload = buildRoomQrCode(room.id);
      const fileName = `room_${sanitizeFileName(room.id)}.png`;
      return {
        type: "room",
        id: room.id,
        label: room.name,
        payload,
        fileName,
        relativePath: fileName,
      };
    });
};

const anchorQrItems = (): GeneratedQr[] => {
  return Object.values(QR_ANCHOR_REGISTRY)
    .sort((a, b) => a.qrId.localeCompare(b.qrId))
    .map((anchor: QrAnchor) => {
      const fileName = `anchor_${sanitizeFileName(anchor.qrId)}.png`;
      return {
        type: "anchor",
        id: anchor.qrId,
        label: anchor.label,
        payload: anchor.qrId,
        fileName,
        relativePath: fileName,
      };
    });
};

const writeQrImage = async (
  outputPath: string,
  payload: string,
  size: number,
  margin: number,
): Promise<void> => {
  await QRCode.toFile(outputPath, payload, {
    type: "png",
    width: size,
    margin,
    errorCorrectionLevel: "H",
    color: {
      dark: "#111827",
      light: "#ffffff",
    },
  });
};

const writeMetadata = async (outDir: string, generated: GeneratedQr[]): Promise<void> => {
  const metadata = {
    generatedAt: new Date().toISOString(),
    total: generated.length,
    rooms: generated.filter((item) => item.type === "room"),
    anchors: generated.filter((item) => item.type === "anchor"),
  };

  const metadataPath = path.join(outDir, "index.json");
  await fs.writeFile(metadataPath, JSON.stringify(metadata, null, 2), "utf-8");
};

const generate = async (): Promise<void> => {
  const options = parseCliArgs();
  const absoluteOutDir = path.resolve(process.cwd(), options.outDir);

  await ensureDir(absoluteOutDir);

  if (options.clean) {
    const removed = await cleanPngFiles(absoluteOutDir);
    console.log(`🧹 Cleaned ${removed} existing PNG file(s) in ${options.outDir}`);
  }

  const generated: GeneratedQr[] = [];

  if (options.include === "rooms" || options.include === "all") {
    generated.push(...roomQrItems());
  }

  if (options.include === "anchors" || options.include === "all") {
    generated.push(...anchorQrItems());
  }

  if (!generated.length) {
    console.log("No QR items selected. Nothing to generate.");
    return;
  }

  for (const item of generated) {
    const outputPath = path.join(absoluteOutDir, item.fileName);
    await writeQrImage(outputPath, item.payload, options.size, options.margin);
  }

  await writeMetadata(absoluteOutDir, generated);

  const roomCount = generated.filter((item) => item.type === "room").length;
  const anchorCount = generated.filter((item) => item.type === "anchor").length;

  console.log("✅ QR generation completed");
  console.log(`   Output dir : ${options.outDir}`);
  console.log(`   Room QR    : ${roomCount}`);
  console.log(`   Anchor QR  : ${anchorCount}`);
  console.log(`   Total PNG  : ${generated.length}`);
  console.log("   Metadata   : index.json");
};

generate().catch((error: unknown) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error(`❌ Failed to generate QR codes: ${message}`);
  process.exit(1);
});
