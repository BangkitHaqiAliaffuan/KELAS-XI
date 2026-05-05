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
type AnchorFloor = -1 | 0 | 1 | 2;
type FloorFilter = AnchorFloor | "all";

type CliOptions = {
  outDir: string;
  include: IncludeMode;
  floor: FloorFilter;
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
  include: "anchors",
  floor: "all",
  size: 512,
  margin: 2,
  clean: false,
};

const normalizeInclude = (value: string | undefined): IncludeMode => {
  if (!value) return "all";
  if (value === "rooms" || value === "anchors" || value === "all") return value;
  throw new Error(`Invalid --include value: "${value}". Use rooms|anchors|all.`);
};

const normalizeFloor = (value: string | undefined): FloorFilter => {
  if (!value || value === "all") return "all";
  if (value === "-1" || value.toLowerCase() === "parking2") return -1;
  if (value === "0" || value.toLowerCase() === "parking") return 0;
  if (value === "1") return 1;
  if (value === "2") return 2;
  throw new Error(`Invalid --floor value: "${value}". Use -1|0|1|2|parking|parking2|all.`);
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

    if (arg === "--floor") {
      options.floor = normalizeFloor(args[i + 1]);
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
  process.stdout.write(
    `\nQR Generator\n\nUsage:\n  npm run qr:generate -- [options]\n\nOptions:\n  --include <rooms|anchors|all>  Which QR sets to generate (default: anchors)\n  --outDir <path>                 Output directory (default: public/images/qr)\n  --size <number>                 PNG width in px (default: 512)\n  --margin <number>               QR margin (default: 2)\n  --clean                         Remove existing PNG files in the selected output directory\n  --help                          Show this help\n`
  );
  process.stdout.write(`  --floor <-1|0|1|2|parking|parking2|all>  Filter QR by floor (default: all)\n`);
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
    if (entry.isFile() && entry.name.toLowerCase().endsWith(".png")) {
      await fs.unlink(path.join(dirPath, entry.name));
      removed += 1;
    }
  }

  return removed;
};

const roomQrItems = (floor: FloorFilter): GeneratedQr[] => {
  let roomIds = Object.keys(roomInfoBySvgId);

  if (floor !== "all") {
    const roomIdsFromAnchors = new Set(
      Object.values(QR_ANCHOR_REGISTRY)
        .filter((anchor) => anchor.floor === floor)
        .map((anchor) => anchor.roomId)
        .filter((roomId) => Boolean(roomInfoBySvgId[roomId]))
    );
    roomIds = roomIds.filter((roomId) => roomIdsFromAnchors.has(roomId));
  }

  return roomIds
    .map((roomId) => roomInfoBySvgId[roomId])
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

const anchorQrItems = (floor: FloorFilter): GeneratedQr[] => {
  return Object.values(QR_ANCHOR_REGISTRY)
    .filter((anchor) => floor === "all" || anchor.floor === floor)
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

const validateAnchorRegistry = (): void => {
  const ids = new Set<string>();
  const problems: string[] = [];

  for (const anchor of Object.values(QR_ANCHOR_REGISTRY)) {
    if (!anchor.qrId.trim()) problems.push("Ada QR anchor tanpa qrId.");
    if (ids.has(anchor.qrId)) problems.push(`QR anchor duplikat: ${anchor.qrId}`);
    ids.add(anchor.qrId);

    if (!anchor.roomId.trim()) problems.push(`${anchor.qrId} belum punya roomId.`);
    if (!Number.isFinite(anchor.svgX) || !Number.isFinite(anchor.svgY)) {
      problems.push(`${anchor.qrId} punya koordinat SVG tidak valid.`);
    }
    if (![-1, 0, 1, 2].includes(anchor.floor)) {
      problems.push(`${anchor.qrId} punya floor tidak dikenal: ${anchor.floor}`);
    }
  }

  if (problems.length) {
    throw new Error(`QR_ANCHOR_REGISTRY tidak valid:\n- ${problems.join("\n- ")}`);
  }
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
    anchorSource: "src/data/hospitalRouteGraph.ts:QR_ANCHOR_REGISTRY",
    anchorRegistryTotal: Object.keys(QR_ANCHOR_REGISTRY).length,
    rooms: generated.filter((item) => item.type === "room"),
    anchors: generated
      .filter((item) => item.type === "anchor")
      .map((item) => {
        const anchor = QR_ANCHOR_REGISTRY[item.id];
        return {
          ...item,
          roomId: anchor.roomId,
          floor: anchor.floor,
          svgX: anchor.svgX,
          svgY: anchor.svgY,
          routeNodeId: anchor.routeNodeId,
        };
      }),
  };

  const metadataPath = path.join(outDir, "index.json");
  await fs.writeFile(metadataPath, JSON.stringify(metadata, null, 2), "utf-8");
};

const generate = async (): Promise<void> => {
  const options = parseCliArgs();
  const absoluteOutDir = path.resolve(process.cwd(), options.outDir);

  validateAnchorRegistry();
  await ensureDir(absoluteOutDir);

  if (options.clean) {
    const removed = await cleanPngFiles(absoluteOutDir);
    process.stdout.write(
      `🧹 Cleaned ${removed} existing PNG file(s) in ${options.outDir}\n`
    );
  }

  const generated: GeneratedQr[] = [];

  if (options.include === "rooms" || options.include === "all") {
    generated.push(...roomQrItems(options.floor));
  }

  if (options.include === "anchors" || options.include === "all") {
    generated.push(...anchorQrItems(options.floor));
  }

  if (!generated.length) {
    process.stdout.write("No QR items selected. Nothing to generate.\n");
    return;
  }

  for (const item of generated) {
    const outputPath = path.join(absoluteOutDir, item.fileName);
    await writeQrImage(outputPath, item.payload, options.size, options.margin);
  }

  await writeMetadata(absoluteOutDir, generated);

  const roomCount = generated.filter((item) => item.type === "room").length;
  const anchorCount = generated.filter((item) => item.type === "anchor").length;

  process.stdout.write("✅ QR generation completed\n");
  process.stdout.write(`   Output dir : ${options.outDir}\n`);
  process.stdout.write(`   Floor      : ${options.floor}\n`);
  process.stdout.write(`   Room QR    : ${roomCount}\n`);
  process.stdout.write(`   Anchor QR  : ${anchorCount}\n`);
  process.stdout.write(`   Total PNG  : ${generated.length}\n`);
  process.stdout.write("   Metadata   : index.json\n");
};

generate().catch((error: unknown) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error(`❌ Failed to generate QR codes: ${message}`);
  process.exit(1);
});
