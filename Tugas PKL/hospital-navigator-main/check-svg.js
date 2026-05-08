import fs from 'fs';
import { JSDOM } from 'jsdom';

const svgString = fs.readFileSync('public/images/hospital-map.svg', 'utf8');
const dom = new JSDOM(svgString);
const svgDoc = dom.window.document;

const nodeLayer = Array.from(svgDoc.querySelectorAll("g")).find((group) => {
  const label = group.getAttribute("inkscape:label");
  return label === "Node Jalan" || label === "Pathfinding Node";
});

if (nodeLayer) {
  Array.from(nodeLayer.querySelectorAll("circle, ellipse")).forEach(element => {
    if (element.id && element.id.includes("Tangga_Evakuasi")) {
        console.log("ID FOUND: " + JSON.stringify(element.id));
    }
  });
}
