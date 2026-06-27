import fs from 'fs';
import { JSDOM } from 'jsdom';
import { buildRoadGraphFromSvg, buildRouteForRooms, ensureGeneratedRoomAnchorNode, getNodeCenterFromSvgElement } from './src/data/hospitalRouteGraph.ts';

console.log("We need to compile hospitalRouteGraph.ts or test it differently.");
