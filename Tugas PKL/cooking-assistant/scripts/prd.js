const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  HeadingLevel, AlignmentType, BorderStyle, WidthType, ShadingType,
  LevelFormat, PageBreak, TabStopType, TabStopPosition
} = require('docx');
const fs = require('fs');

const BRAND = "1A1A1A";
const AMBER = "F5A623";
const AMBER_LIGHT = "FEF3DC";
const TEAL = "0F6E56";
const TEAL_LIGHT = "E1F5EE";
const GRAY_LIGHT = "F5F5F5";
const GRAY_MID = "CCCCCC";
const GRAY_DARK = "666666";
const WHITE = "FFFFFF";
const RED_LIGHT = "FDECEA";
const RED = "C0392B";
const GREEN_LIGHT = "EAF5EA";
const GREEN = "1E7E34";

const border = { style: BorderStyle.SINGLE, size: 1, color: GRAY_MID };
const borders = { top: border, bottom: border, left: border, right: border };
const noBorder = { style: BorderStyle.NONE, size: 0, color: "FFFFFF" };
const noBorders = { top: noBorder, bottom: noBorder, left: noBorder, right: noBorder };

function h1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    spacing: { before: 360, after: 120 },
    border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: AMBER, space: 6 } },
    children: [new TextRun({ text, font: "Arial", size: 32, bold: true, color: BRAND })]
  });
}

function h2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    spacing: { before: 280, after: 100 },
    children: [new TextRun({ text, font: "Arial", size: 26, bold: true, color: BRAND })]
  });
}

function h3(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_3,
    spacing: { before: 200, after: 80 },
    children: [new TextRun({ text, font: "Arial", size: 22, bold: true, color: GRAY_DARK })]
  });
}

function body(text, options = {}) {
  return new Paragraph({
    spacing: { before: 60, after: 80 },
    children: [new TextRun({ text, font: "Arial", size: 22, color: BRAND, ...options })]
  });
}

function bullet(text, bold_prefix = "") {
  return new Paragraph({
    numbering: { reference: "bullets", level: 0 },
    spacing: { before: 40, after: 40 },
    children: [
      ...(bold_prefix ? [new TextRun({ text: bold_prefix + " ", font: "Arial", size: 22, bold: true, color: BRAND })] : []),
      new TextRun({ text, font: "Arial", size: 22, color: BRAND })
    ]
  });
}

function subbullet(text) {
  return new Paragraph({
    numbering: { reference: "subbullets", level: 0 },
    spacing: { before: 30, after: 30 },
    children: [new TextRun({ text, font: "Arial", size: 20, color: GRAY_DARK })]
  });
}

function spacer(lines = 1) {
  return new Paragraph({ children: [new TextRun("")], spacing: { before: 80 * lines, after: 0 } });
}

function infoBox(title, lines, color = AMBER_LIGHT, borderColor = AMBER) {
  const rows = [
    new TableRow({
      children: [new TableCell({
        borders: { top: { style: BorderStyle.SINGLE, size: 8, color: borderColor }, bottom: noBorder, left: noBorder, right: noBorder },
        shading: { fill: color, type: ShadingType.CLEAR },
        margins: { top: 120, bottom: 40, left: 160, right: 160 },
        width: { size: 9360, type: WidthType.DXA },
        children: [new Paragraph({ children: [new TextRun({ text: title, font: "Arial", size: 22, bold: true, color: BRAND })] })]
      })]
    }),
    ...lines.map(line => new TableRow({
      children: [new TableCell({
        borders: { top: noBorder, bottom: noBorder, left: noBorder, right: noBorder },
        shading: { fill: color, type: ShadingType.CLEAR },
        margins: { top: 30, bottom: 30, left: 160, right: 160 },
        width: { size: 9360, type: WidthType.DXA },
        children: [new Paragraph({ spacing: { before: 20, after: 20 }, children: [new TextRun({ text: line, font: "Arial", size: 20, color: BRAND })] })]
      })]
    })),
    new TableRow({
      children: [new TableCell({
        borders: { top: noBorder, bottom: { style: BorderStyle.SINGLE, size: 1, color: borderColor }, left: noBorder, right: noBorder },
        shading: { fill: color, type: ShadingType.CLEAR },
        margins: { top: 60, bottom: 0, left: 160, right: 160 },
        width: { size: 9360, type: WidthType.DXA },
        children: [new Paragraph({ children: [new TextRun("")] })]
      })]
    })
  ];
  return new Table({ width: { size: 9360, type: WidthType.DXA }, columnWidths: [9360], rows });
}

function statusBadge(label, fill, textColor) {
  return new TableCell({
    borders: noBorders,
    shading: { fill, type: ShadingType.CLEAR },
    margins: { top: 60, bottom: 60, left: 120, right: 120 },
    width: { size: 1200, type: WidthType.DXA },
    verticalAlign: "center",
    children: [new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [new TextRun({ text: label, font: "Arial", size: 18, bold: true, color: textColor })]
    })]
  });
}

function featureRow(id, name, desc, priority, status) {
  const priorityFill = priority === "P0" ? RED_LIGHT : priority === "P1" ? AMBER_LIGHT : TEAL_LIGHT;
  const priorityColor = priority === "P0" ? RED : priority === "P1" ? "854F0B" : TEAL;
  const statusFill = status === "MVP" ? AMBER_LIGHT : status === "v1.1" ? TEAL_LIGHT : GRAY_LIGHT;
  const statusColor = status === "MVP" ? "854F0B" : status === "v1.1" ? TEAL : GRAY_DARK;

  return new TableRow({
    children: [
      new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 600, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: id, font: "Arial", size: 20, bold: true, color: GRAY_DARK })] })] }),
      new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1800, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: name, font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
      new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4560, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: desc, font: "Arial", size: 20, color: BRAND })] })] }),
      new TableCell({ borders, shading: { fill: priorityFill, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 80, right: 80 }, width: { size: 600, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: priority, font: "Arial", size: 18, bold: true, color: priorityColor })] })] }),
      new TableCell({ borders, shading: { fill: statusFill, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 80, right: 80 }, width: { size: 1200, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: status, font: "Arial", size: 18, bold: true, color: statusColor })] })] }),
    ]
  });
}

function tableHeader(cells, widths) {
  return new TableRow({
    tableHeader: true,
    children: cells.map((text, i) => new TableCell({
      borders,
      shading: { fill: BRAND, type: ShadingType.CLEAR },
      margins: { top: 100, bottom: 100, left: 120, right: 120 },
      width: { size: widths[i], type: WidthType.DXA },
      children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text, font: "Arial", size: 20, bold: true, color: WHITE })] })]
    }))
  });
}

const doc = new Document({
  numbering: {
    config: [
      {
        reference: "bullets",
        levels: [{
          level: 0, format: LevelFormat.BULLET, text: "\u2022",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 480, hanging: 240 } } }
        }]
      },
      {
        reference: "subbullets",
        levels: [{
          level: 0, format: LevelFormat.BULLET, text: "\u25CB",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 840, hanging: 240 } } }
        }]
      },
      {
        reference: "numbers",
        levels: [{
          level: 0, format: LevelFormat.DECIMAL, text: "%1.",
          alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 480, hanging: 240 } } }
        }]
      }
    ]
  },
  styles: {
    default: {
      document: { run: { font: "Arial", size: 22 } }
    },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true, run: { size: 32, bold: true, font: "Arial" }, paragraph: { spacing: { before: 360, after: 120 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true, run: { size: 26, bold: true, font: "Arial" }, paragraph: { spacing: { before: 280, after: 100 }, outlineLevel: 1 } },
      { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "Normal", quickFormat: true, run: { size: 22, bold: true, font: "Arial" }, paragraph: { spacing: { before: 200, after: 80 }, outlineLevel: 2 } },
    ]
  },
  sections: [{
    properties: {
      page: {
        size: { width: 11906, height: 16838 },
        margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
      }
    },
    headers: {
      default: {
        options: {
          children: [
            new Paragraph({
              border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: AMBER, space: 4 } },
              spacing: { after: 0 },
              children: [
                new TextRun({ text: "ChefEye AI  ", font: "Arial", size: 20, bold: true, color: AMBER }),
                new TextRun({ text: "Product Requirements Document", font: "Arial", size: 20, color: GRAY_DARK }),
                new TextRun({ text: "\tv1.0  |  2026", font: "Arial", size: 18, color: GRAY_DARK })
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }]
            })
          ]
        }
      }
    },
    footers: {
      default: {
        options: {
          children: [
            new Paragraph({
              border: { top: { style: BorderStyle.SINGLE, size: 2, color: GRAY_MID, space: 4 } },
              spacing: { before: 0 },
              children: [
                new TextRun({ text: "Confidential — Internal Use Only", font: "Arial", size: 18, color: GRAY_DARK }),
                new TextRun({ text: "\tPage ", font: "Arial", size: 18, color: GRAY_DARK }),
                new TextRun({ 
                  children: ["PAGE_NUMBER"],
                  font: "Arial", 
                  size: 18, 
                  color: GRAY_DARK 
                })
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }]
            })
          ]
        }
      }
    },
    children: [

      // ── COVER PAGE ──
      spacer(4),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { before: 0, after: 80 },
        children: [new TextRun({ text: "ChefEye AI", font: "Arial", size: 72, bold: true, color: AMBER })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { before: 0, after: 200 },
        children: [new TextRun({ text: "Product Requirements Document", font: "Arial", size: 36, color: BRAND })]
      }),
      new Table({
        width: { size: 4000, type: WidthType.DXA },
        columnWidths: [2000, 2000],
        alignment: AlignmentType.CENTER,
        rows: [
          new TableRow({ children: [
            new TableCell({ borders: noBorders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Versi", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
            new TableCell({ borders: noBorders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "1.0 — MVP", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders: noBorders, shading: { fill: WHITE, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Status", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
            new TableCell({ borders: noBorders, shading: { fill: WHITE, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "In Development", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders: noBorders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Tanggal", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
            new TableCell({ borders: noBorders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Mei 2026", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders: noBorders, shading: { fill: WHITE, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Platform", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
            new TableCell({ borders: noBorders, shading: { fill: WHITE, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Web PWA (Mobile-first)", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
          ]}),
        ]
      }),
      spacer(1),
      new Paragraph({ children: [new PageBreak()] }),

      // ── 1. EXECUTIVE SUMMARY ──
      h1("1. Executive Summary"),
      body("ChefEye AI adalah asisten memasak multimodal berbasis web yang dirancang untuk memberikan bimbingan memasak secara real-time dan hands-free. Produk ini menjembatani celah antara resep teks statis dengan eksekusi dinamis di dapur menggunakan integrasi Computer Vision dan AI Voice."),
      spacer(1),
      infoBox("Proposisi Nilai Utama", [
        "Pengguna dapat memasak tanpa menyentuh layar, seluruhnya dikendalikan melalui suara.",
        "AI mengidentifikasi bahan secara visual dan memberikan instruksi kontekstual berikutnya.",
        "Konteks percakapan dipertahankan sepanjang sesi memasak untuk pengalaman yang kohesif.",
        "Dibangun dengan filosofi zero-budget — tidak ada biaya operasional untuk skala MVP."
      ]),
      spacer(1),

      // ── 2. PROBLEM STATEMENT ──
      h1("2. Problem Statement"),
      h2("2.1 Masalah Utama Pengguna"),
      bullet("Memasak sambil melihat layar ponsel sangat merepotkan karena tangan kotor atau basah."),
      bullet("Pemula kesulitan mengidentifikasi bahan atau teknik memotong yang benar tanpa bimbingan visual."),
      bullet("Resep teks statis tidak responsif terhadap kondisi aktual di dapur pengguna."),
      bullet("Tidak ada solusi yang menggabungkan vision, voice, dan context dalam satu pengalaman seamless."),
      spacer(1),
      h2("2.2 Dampak Masalah"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [3000, 3013, 3013],
        rows: [
          tableHeader(["Segmen Pengguna", "Pain Point", "Dampak"], [3000, 3013, 3013]),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Pemula memasak", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Tidak bisa identifikasi bahan segar vs busuk", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Makanan gagal, bahan terbuang", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Koki rumahan aktif", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Harus menyentuh layar berkali-kali", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Layar kotor, alur masak terganggu", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Pengguna dengan keterbatasan fisik", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Sulit mengoperasikan app konvensional", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Eksklusi dari pengalaman memasak mandiri", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
        ]
      }),
      spacer(1),

      // ── 3. GOALS & SUCCESS METRICS ──
      h1("3. Goals & Success Metrics"),
      h2("3.1 Tujuan Bisnis"),
      bullet("Meluncurkan MVP yang fungsional dalam 8 minggu pertama pengembangan."),
      bullet("Mendapatkan 500 pengguna aktif bulanan dalam 3 bulan pertama pasca-launch."),
      bullet("Mempertahankan zero operational cost hingga pengguna mencapai 1.000 MAU."),
      bullet("Membangun fondasi teknis yang dapat di-scale ke fitur premium di masa depan."),
      spacer(1),
      h2("3.2 Key Success Metrics"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [2000, 2500, 2263, 2263],
        rows: [
          tableHeader(["Metrik", "Definisi", "Target MVP", "Target v1.1"], [2000, 2500, 2263, 2263]),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Vision Latency", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Waktu dari snapshot ke respons AI", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "< 2 detik", font: "Arial", size: 20, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "< 1 detik", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Ingredient Accuracy", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "% identifikasi bahan yang benar", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "> 85%", font: "Arial", size: 20, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "> 93%", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Session Completion", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "% sesi yang selesai sampai langkah terakhir", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "> 60%", font: "Arial", size: 20, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "> 75%", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Voice Recognition Rate", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "% perintah suara yang dipahami dengan benar", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "> 80%", font: "Arial", size: 20, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2263, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "> 90%", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
          ]}),
        ]
      }),
      spacer(1),

      // ── 4. USER PERSONAS ──
      h1("4. User Personas"),
      h2("4.1 Persona Utama — Pemula Antusias"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [4513, 4513],
        rows: [
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 100, bottom: 100, left: 160, right: 160 }, width: { size: 4513, type: WidthType.DXA }, children: [
              new Paragraph({ spacing: { after: 60 }, children: [new TextRun({ text: "Rina, 24 tahun — Karyawan Kantoran", font: "Arial", size: 22, bold: true, color: BRAND })] }),
              new Paragraph({ spacing: { after: 40 }, children: [new TextRun({ text: "Baru belajar masak sejak kos. Sering gagal karena tidak tahu takaran yang benar atau bahan yang sudah tidak segar.", font: "Arial", size: 20, color: BRAND })] }),
            ]}),
            new TableCell({ borders, margins: { top: 100, bottom: 100, left: 160, right: 160 }, width: { size: 4513, type: WidthType.DXA }, children: [
              new Paragraph({ spacing: { after: 40 }, children: [new TextRun({ text: "Goals:", font: "Arial", size: 20, bold: true, color: TEAL })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 30 }, children: [new TextRun({ text: "Masak tanpa takut gagal", font: "Arial", size: 20, color: BRAND })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 60 }, children: [new TextRun({ text: "Tahu bahan masih layak pakai atau tidak", font: "Arial", size: 20, color: BRAND })] }),
              new Paragraph({ spacing: { after: 40 }, children: [new TextRun({ text: "Pain Points:", font: "Arial", size: 20, bold: true, color: RED })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 30 }, children: [new TextRun({ text: "Tidak bisa pegang HP saat tangan berminyak", font: "Arial", size: 20, color: BRAND })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 30 }, children: [new TextRun({ text: "Resep di YouTube tidak bisa di-pause mudah", font: "Arial", size: 20, color: BRAND })] }),
            ]}),
          ]}),
        ]
      }),
      spacer(1),
      h2("4.2 Persona Sekunder — Koki Rumahan Aktif"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [4513, 4513],
        rows: [
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 100, bottom: 100, left: 160, right: 160 }, width: { size: 4513, type: WidthType.DXA }, children: [
              new Paragraph({ spacing: { after: 60 }, children: [new TextRun({ text: "Budi, 38 tahun — Memasak untuk Keluarga", font: "Arial", size: 22, bold: true, color: BRAND })] }),
              new Paragraph({ spacing: { after: 40 }, children: [new TextRun({ text: "Rutin memasak 5x seminggu. Mahir tapi ingin eksplorasi resep baru dengan bahan yang ada di kulkas.", font: "Arial", size: 20, color: BRAND })] }),
            ]}),
            new TableCell({ borders, margins: { top: 100, bottom: 100, left: 160, right: 160 }, width: { size: 4513, type: WidthType.DXA }, children: [
              new Paragraph({ spacing: { after: 40 }, children: [new TextRun({ text: "Goals:", font: "Arial", size: 20, bold: true, color: TEAL })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 30 }, children: [new TextRun({ text: "Generate resep dari bahan yang tersedia", font: "Arial", size: 20, color: BRAND })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 60 }, children: [new TextRun({ text: "Bimbingan hands-free tanpa gangguan", font: "Arial", size: 20, color: BRAND })] }),
              new Paragraph({ spacing: { after: 40 }, children: [new TextRun({ text: "Pain Points:", font: "Arial", size: 20, bold: true, color: RED })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 30 }, children: [new TextRun({ text: "App resep tidak fleksibel dengan bahan improvisasi", font: "Arial", size: 20, color: BRAND })] }),
              new Paragraph({ numbering: { reference: "bullets", level: 0 }, spacing: { before: 30, after: 30 }, children: [new TextRun({ text: "Tidak ada yang ingat progres memasak sebelumnya", font: "Arial", size: 20, color: BRAND })] }),
            ]}),
          ]}),
        ]
      }),
      spacer(1),

      // ── 5. FEATURES ──
      h1("5. Feature Requirements"),
      h2("5.1 Feature Registry"),
      body("Prioritas: P0 = Must Have (MVP blocker), P1 = Should Have, P2 = Nice to Have"),
      spacer(1),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [600, 1800, 4560, 600, 1200],
        rows: [
          tableHeader(["ID", "Fitur", "Deskripsi", "Prior.", "Target"], [600, 1800, 4560, 600, 1200]),
          featureRow("F-01", "Real-time Vision ID", "Kamera mengidentifikasi bahan makanan secara instan menggunakan Groq Vision API. Menampilkan nama bahan dan confidence score sebagai overlay di viewfinder.", "P0", "MVP"),
          featureRow("F-02", "Voice Wake Word", "Pengguna mengaktifkan asisten dengan kata kunci 'Hey Chef' tanpa menyentuh layar. Sistem selalu mendengarkan di background saat sesi aktif.", "P0", "MVP"),
          featureRow("F-03", "Step-by-Step Guidance", "AI memberikan instruksi memasak per langkah secara terurut. Setiap instruksi disesuaikan dengan konteks visual yang terdeteksi di kamera.", "P0", "MVP"),
          featureRow("F-04", "Contextual Memory", "AI mengingat bahan yang sudah diproses dalam sesi berjalan. State disimpan di Zustand dan diinjeksikan ke setiap API call sebagai system context.", "P0", "MVP"),
          featureRow("F-05", "TTS Response", "Seluruh instruksi AI disuarakan melalui Web Speech API (speechSynthesis). Pengguna tidak perlu membaca layar selama memasak.", "P0", "MVP"),
          featureRow("F-06", "Recipe Browser", "Pengguna dapat mencari dan memilih resep dari TheMealDB API. Tersedia filter berdasarkan kategori dan bahan utama.", "P0", "MVP"),
          featureRow("F-07", "Ingredient Scanner", "Mode scan khusus untuk mengidentifikasi semua bahan sebelum memasak. Hasil ditampilkan sebagai card dengan info kesegaran.", "P1", "MVP"),
          featureRow("F-08", "Local Recipe Cache", "Resep yang pernah dibuka di-cache di IndexedDB untuk akses offline. 20-30 resep Indonesia populer dibundle sebagai static JSON.", "P1", "MVP"),
          featureRow("F-09", "Improvisation Mode", "User mengarahkan kamera ke semua bahan di kulkas, AI generate resep on-the-fly berdasarkan visual input tanpa teks.", "P1", "v1.1"),
          featureRow("F-10", "Freshness Detective", "AI menilai kesegaran visual bahan (layu, busuk, segar) dan memberikan rekomendasi apakah bahan layak digunakan.", "P1", "v1.1"),
          featureRow("F-11", "Cooking Session Log", "Setiap sesi memasak tersimpan dengan timestamp dan foto bahan. User dapat review sesi sebelumnya untuk analisis.", "P2", "v1.1"),
          featureRow("F-12", "Barcode Scanner", "Integrasi Open Food Facts API untuk identifikasi produk kemasan melalui scan barcode. Menampilkan komposisi dan info nutrisi.", "P2", "v2.0"),
          featureRow("F-13", "Regional Dialect", "Dukungan perintah suara dalam bahasa daerah (Jawa, Sunda) sebagai diferensiasi pasar Indonesia.", "P2", "v2.0"),
        ]
      }),
      spacer(1),

      // ── 6. TECHNICAL ARCHITECTURE ──
      h1("6. Technical Architecture"),
      h2("6.1 Tech Stack"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [2000, 3013, 4013],
        rows: [
          tableHeader(["Layer", "Teknologi", "Justifikasi"], [2000, 3013, 4013]),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Frontend", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Next.js (App Router) + Tailwind CSS", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "SSR + PWA support out-of-the-box, mobile-first", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "State Management", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Zustand + IndexedDB (via zustand/persist)", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Local-first, zero backend, works offline", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Vision AI (Primary)", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Groq API + Llama 4 Scout 17b", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Inferensi visual tercepat (~500 tok/s), free tier generous", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Vision AI (Fallback)", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Google Gemini 1.5 Flash", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Digunakan hanya saat Groq rate limit tercapai", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Voice Engine", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Web Speech API (STT + TTS)", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Native browser API, zero cost, no latency overhead", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Recipe Data", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "TheMealDB API + Local JSON", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "TheMealDB 100% gratis tanpa API key, JSON lokal untuk resep Indonesia", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Deployment", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Vercel (Free Tier, PWA enabled)", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4013, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Serverless, edge network global, CI/CD otomatis dari GitHub", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
        ]
      }),
      spacer(1),
      h2("6.2 Data Architecture"),
      body("ChefEye AI menggunakan pendekatan local-first. Seluruh state runtime dikelola di client menggunakan Zustand, dengan persistence ke IndexedDB untuk data yang perlu bertahan antar sesi (inventory bahan). Session data bersifat volatile dan direset setiap sesi baru dimulai."),
      spacer(1),
      infoBox("Prinsip Local-First Architecture", [
        "Session state (Zustand): volatile, reset setiap sesi — currentStep, processedIngredients, lastVisualContext",
        "Inventory (IndexedDB): persisten antar sesi — daftar bahan yang pernah dideteksi dan dikonfirmasi",
        "Recipe cache (IndexedDB): resep yang pernah dibuka di-cache untuk akses offline",
        "Semua API call ke Groq menyertakan session context sebagai system prompt injection",
        "Upgrade ke Supabase sync hanya dilakukan jika ada kebutuhan multi-device atau social features"
      ], TEAL_LIGHT, TEAL),
      spacer(1),
      h2("6.3 Context Management Strategy"),
      body("Setiap API call ke Groq menyertakan objek cookingSession sebagai bagian dari system prompt. Ini memastikan AI selalu sadar konteks sesi tanpa memerlukan conversation history yang panjang."),
      spacer(1),
      h2("6.4 Vision Optimization"),
      bullet("Pixel diff detection: frame dikirim ke API hanya jika terdapat perubahan visual > 15% dari frame sebelumnya."),
      bullet("Frame compression: snapshot diresize ke 512x512 sebelum dikirim untuk meminimalkan bandwidth."),
      bullet("Snapshot interval: minimum 3 detik antara dua API call vision untuk menghindari rate limit."),
      bullet("Bounding box rendering dilakukan di frontend menggunakan Canvas API, bukan dari API response."),
      spacer(1),

      // ── 7. UX REQUIREMENTS ──
      h1("7. UX & Accessibility Requirements"),
      h2("7.1 Core UX Principles"),
      bullet("Hands-free first:", "Setiap aksi utama harus dapat dilakukan tanpa menyentuh layar."),
      bullet("Glanceability:", "Informasi kritis harus dapat dibaca dalam 1 detik dari jarak lengan."),
      bullet("Error recovery:", "Pesan error harus actionable dan dalam bahasa yang ramah (bukan kode teknis)."),
      bullet("Offline resilience:", "App harus tetap berfungsi minimal untuk resep yang sudah di-cache meskipun tanpa koneksi."),
      spacer(1),
      h2("7.2 Screen Hierarchy"),
      bullet("Home Screen — Entry point, CTA tunggal, 3 feature highlight."),
      bullet("Active Cooking Screen — Layar utama. Camera viewfinder mendominasi 60% area. Step card floating di bawah."),
      bullet("Ingredient Scanner Screen — Full-screen camera mode dengan hasil identifikasi sliding dari bawah."),
      bullet("Recipe Browser Screen — Grid resep dengan search dan filter kategori."),
      bullet("Recipe Detail Screen — Hero image, ingredient checklist, step timeline, sticky CTA."),
      spacer(1),
      h2("7.3 Accessibility Standards"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [3000, 6026],
        rows: [
          tableHeader(["Standar", "Implementasi"], [3000, 6026]),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Ukuran touch target", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 6026, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Minimum 44x44px untuk semua elemen interaktif, 56px untuk CTA utama", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Kontras warna", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 6026, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Minimum 4.5:1 untuk body text (WCAG AA), 3:1 untuk large text", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Ukuran font minimum", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 6026, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "16px untuk body text, 14px untuk label. Step instruction menggunakan 20px+", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Screen reader", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 6026, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Semua overlay kamera harus memiliki aria-label yang deskriptif", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Reduced motion", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 6026, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Semua animasi harus menghormati prefers-reduced-motion media query", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 3000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Focus indicator", font: "Arial", size: 20, bold: true, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 6026, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "2px solid amber (#F5A623), 2px offset untuk navigasi keyboard yang visible", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
        ]
      }),
      spacer(1),

      // ── 8. SPRINT PLAN ──
      h1("8. Development Roadmap"),
      h2("8.1 Sprint Plan — MVP (8 Minggu)"),
      spacer(1),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [900, 1800, 4326, 2000],
        rows: [
          tableHeader(["Sprint", "Durasi", "Deliverable", "Fitur (ID)"], [900, 1800, 4326, 2000]),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: RED_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 900, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "S1", font: "Arial", size: 22, bold: true, color: RED })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1800, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Minggu 1–2", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Foundation: Next.js setup, Zustand store, session context object, UI shell semua screen", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "F-04, F-08", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 900, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "S2", font: "Arial", size: 22, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1800, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Minggu 3–4", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Vision Core: Groq API integration, pixel diff detection, camera overlay, ingredient chip", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "F-01, F-07", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 900, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "S3", font: "Arial", size: 22, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1800, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Minggu 5–6", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Voice Engine: Wake word, STT intent parsing, TTS response, voice state machine", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "F-02, F-05", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 900, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "S4", font: "Arial", size: 22, bold: true, color: TEAL })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1800, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Minggu 7–8", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 4326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Recipe & Polish: TheMealDB integration, step-by-step guidance, testing, PWA config, Vercel deploy", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2000, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "F-03, F-06", font: "Arial", size: 20, color: GRAY_DARK })] })] }),
          ]}),
        ]
      }),
      spacer(1),

      // ── 9. RISKS ──
      h1("9. Risks & Mitigations"),
      new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [2500, 1200, 5326],
        rows: [
          tableHeader(["Risiko", "Dampak", "Mitigasi"], [2500, 1200, 5326]),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Groq rate limit tercapai saat jam sibuk", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: RED_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1200, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Tinggi", font: "Arial", size: 20, bold: true, color: RED })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 5326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Request queue + automatic fallback ke Gemini. Pixel diff mengurangi frekuensi API call 70%.", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Web Speech API tidak konsisten di Firefox/Android", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1200, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Sedang", font: "Arial", size: 20, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 5326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Fallback ke whisper.cpp via WASM untuk offline STT. Tampilkan warning jika browser tidak support.", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Akurasi rendah di dapur berasap/gelap", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: AMBER_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1200, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Sedang", font: "Arial", size: 20, bold: true, color: "854F0B" })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 5326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Tampilkan indikator kualitas cahaya. Saran verbal jika kondisi buruk: 'Cahaya kurang, coba dekatkan bahan ke cahaya.'", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "TheMealDB tidak memiliki resep Indonesia", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1200, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Rendah", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
            new TableCell({ borders, shading: { fill: GRAY_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 5326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Bundle 25-30 resep Indonesia populer sebagai static JSON lokal. TheMealDB untuk resep internasional.", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
          new TableRow({ children: [
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 2500, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Vercel bandwidth limit 100GB/bulan", font: "Arial", size: 20, color: BRAND })] })] }),
            new TableCell({ borders, shading: { fill: TEAL_LIGHT, type: ShadingType.CLEAR }, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 1200, type: WidthType.DXA }, children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: "Rendah", font: "Arial", size: 20, bold: true, color: TEAL })] })] }),
            new TableCell({ borders, margins: { top: 80, bottom: 80, left: 120, right: 120 }, width: { size: 5326, type: WidthType.DXA }, children: [new Paragraph({ children: [new TextRun({ text: "Kompresi gambar agresif sebelum upload. PWA caching mengurangi request berulang.", font: "Arial", size: 20, color: BRAND })] })] }),
          ]}),
        ]
      }),
      spacer(1),

      // ── 10. OPEN QUESTIONS ──
      h1("10. Open Questions & Decisions Pending"),
      infoBox("Pertanyaan yang Perlu Dijawab Sebelum Sprint 3", [
        "Q1: Apakah wake word 'Hey Chef' diproses sepenuhnya di client (whisper.cpp WASM) atau tetap via Web Speech API?",
        "Q2: Bahasa default instruksi AI — Bahasa Indonesia penuh, atau mixed dengan English untuk istilah teknis memasak?",
        "Q3: Apakah sesi memasak perlu PIN atau passcode untuk mencegah reset tidak sengaja saat cooking mode aktif?",
        "Q4: Batas jumlah resep lokal Indonesia yang di-bundle — apakah 25 resep cukup untuk validasi pasar awal?",
        "Q5: Kapan threshold upgrade dari local-first ke Supabase hybrid? Apakah di 500 MAU atau ada trigger lain?"
      ], RED_LIGHT, RED),
      spacer(1),

      // ── 11. APPENDIX ──
      h1("11. Appendix"),
      h2("11.1 API Reference"),
      bullet("TheMealDB: https://www.themealdb.com/api.php — Gratis, tanpa API key, 300+ resep"),
      bullet("Groq API: https://console.groq.com — Llama 4 Scout 17b, free tier 30 req/menit"),
      bullet("Gemini API: https://ai.google.dev — Gemini 1.5 Flash, fallback vision"),
      bullet("Open Food Facts: https://world.openfoodfacts.org/api — Barcode lookup, 3 juta+ produk"),
      spacer(1),
      h2("11.2 Design References"),
      bullet("Figma/Stitch Design System: Dark theme, Amber #F5A623 accent, DM Sans typography"),
      bullet("Accessibility: WCAG 2.1 Level AA compliance target"),
      bullet("Responsive breakpoints: 375px (mobile primary), 768px (tablet), 1280px (desktop)"),
      spacer(1),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { before: 480 },
        children: [new TextRun({ text: "— End of Document —", font: "Arial", size: 20, color: GRAY_DARK, italics: true })]
      }),
    ]
  }]
});

Packer.toBuffer(doc).then(buffer => {
  const path = require('path');
  const outputPath = path.join(__dirname, '..', 'ChefEye_PRD_v1.0.docx');
  fs.writeFileSync(outputPath, buffer);
  console.log('✅ PRD created successfully!');
  console.log('📄 Location:', outputPath);
});
