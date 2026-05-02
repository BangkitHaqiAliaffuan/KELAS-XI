export type TurnType = "straight" | "turn_left" | "turn_right" | "u_turn" | "arrive";

export interface NavigationStep {
  index: number;
  type: TurnType;
  fromPoint: { x: number; y: number };
  pivotPoint: { x: number; y: number };
  toPoint: { x: number; y: number };
  distanceToNext: number;
  cumulativeDistance: number;
  angleChange: number;
  label: string;
  nextQrHint?: string;
}

export const ANGLE_THRESHOLD = 25;

const SIMPLIFY_EPSILON = 3;

const distance = (a: { x: number; y: number }, b: { x: number; y: number }): number => {
  return Math.hypot(a.x - b.x, a.y - b.y);
};

const perpendicularDistance = (
  point: { x: number; y: number },
  lineStart: { x: number; y: number },
  lineEnd: { x: number; y: number }
): number => {
  const lineLength = distance(lineStart, lineEnd);
  if (lineLength === 0) return distance(point, lineStart);

  const area = Math.abs(
    (lineEnd.x - lineStart.x) * (lineStart.y - point.y) -
      (lineStart.x - point.x) * (lineEnd.y - lineStart.y)
  );
  return area / lineLength;
};

const douglasPeucker = (
  points: Array<{ x: number; y: number }>,
  epsilon: number
): Array<{ x: number; y: number }> => {
  if (points.length < 3) return points;

  let maxDistance = 0;
  let index = 0;

  for (let i = 1; i < points.length - 1; i += 1) {
    const d = perpendicularDistance(points[i], points[0], points[points.length - 1]);
    if (d > maxDistance) {
      maxDistance = d;
      index = i;
    }
  }

  if (maxDistance > epsilon) {
    const left = douglasPeucker(points.slice(0, index + 1), epsilon);
    const right = douglasPeucker(points.slice(index), epsilon);
    return [...left.slice(0, -1), ...right];
  }

  return [points[0], points[points.length - 1]];
};

const simplifyRoutePoints = (
  points: Array<{ x: number; y: number }>
): Array<{ x: number; y: number }> => {
  if (points.length <= 2) return points;
  return douglasPeucker(points, SIMPLIFY_EPSILON);
};

const signedTurnAngle = (
  prev: { x: number; y: number },
  curr: { x: number; y: number },
  next: { x: number; y: number }
): number => {
  const v1 = { x: curr.x - prev.x, y: curr.y - prev.y };
  const v2 = { x: next.x - curr.x, y: next.y - curr.y };

  const cross = v1.x * v2.y - v1.y * v2.x;
  const dot = v1.x * v2.x + v1.y * v2.y;
  const raw = (Math.atan2(cross, dot) * 180) / Math.PI;

  return raw;
};

const classifyTurn = (angleChange: number): TurnType => {
  const magnitude = Math.abs(angleChange);

  if (magnitude >= 150) return "u_turn";
  if (magnitude < ANGLE_THRESHOLD) return "straight";

  return angleChange < 0 ? "turn_left" : "turn_right";
};

const labelForTurn = (turnType: TurnType, angleChange: number): string => {
  if (turnType === "arrive") return "Anda telah tiba di tujuan";
  if (turnType === "u_turn") return "Putar balik";

  if (turnType === "straight") {
    const magnitude = Math.abs(angleChange);
    if (magnitude >= ANGLE_THRESHOLD && magnitude < 60) {
      return angleChange < 0 ? "Belok kiri ringan" : "Belok kanan ringan";
    }
    return "Jalan lurus";
  }

  return turnType === "turn_left" ? "Belok kiri" : "Belok kanan";
};

const mergeConsecutiveStraights = (steps: NavigationStep[]): NavigationStep[] => {
  if (!steps.length) return steps;

  const merged: NavigationStep[] = [];

  steps.forEach((step) => {
    const prev = merged[merged.length - 1];

    if (prev && prev.type === "straight" && step.type === "straight") {
      prev.toPoint = step.toPoint;
      prev.distanceToNext += step.distanceToNext;
      prev.cumulativeDistance = step.cumulativeDistance;
      prev.angleChange = step.angleChange;
      prev.label = step.label;
      return;
    }

    merged.push({ ...step });
  });

  return merged.map((step, idx) => ({ ...step, index: idx }));
};

export const buildNavigationSteps = (
  routePoints: { x: number; y: number }[]
): NavigationStep[] => {
  if (routePoints.length < 2) return [];

  const simplified = simplifyRoutePoints(routePoints);
  if (simplified.length < 2) return [];

  const steps: NavigationStep[] = [];
  let cumulativeDistance = 0;

  if (simplified.length === 2) {
    const directDistance = distance(simplified[0], simplified[1]);
    steps.push({
      index: 0,
      type: "straight",
      fromPoint: simplified[0],
      pivotPoint: simplified[0],
      toPoint: simplified[1],
      distanceToNext: directDistance,
      cumulativeDistance: directDistance,
      angleChange: 0,
      label: "Jalan lurus",
      nextQrHint: "Cari QR di persimpangan terdekat untuk kalibrasi posisi.",
    });

    steps.push({
      index: 1,
      type: "arrive",
      fromPoint: simplified[0],
      pivotPoint: simplified[1],
      toPoint: simplified[1],
      distanceToNext: 0,
      cumulativeDistance: directDistance,
      angleChange: 0,
      label: "Anda telah tiba di tujuan",
    });

    return steps;
  }

  for (let i = 1; i < simplified.length - 1; i += 1) {
    const fromPoint = simplified[i - 1];
    const pivotPoint = simplified[i];
    const toPoint = simplified[i + 1];

    const segmentDistance = distance(pivotPoint, toPoint);
    cumulativeDistance += segmentDistance;

    const angleChange = signedTurnAngle(fromPoint, pivotPoint, toPoint);
    const turnType = classifyTurn(angleChange);

    steps.push({
      index: steps.length,
      type: turnType,
      fromPoint,
      pivotPoint,
      toPoint,
      distanceToNext: segmentDistance,
      cumulativeDistance,
      angleChange,
      label: labelForTurn(turnType, angleChange),
      nextQrHint: turnType !== "straight" ? "Cari QR di persimpangan ini untuk kalibrasi." : undefined,
    });
  }

  const merged = mergeConsecutiveStraights(steps);
  const finalPivot = simplified[simplified.length - 1];
  const beforeFinal = simplified[simplified.length - 2];
  const finalDistance = distance(beforeFinal, finalPivot);

  merged.push({
    index: merged.length,
    type: "arrive",
    fromPoint: beforeFinal,
    pivotPoint: finalPivot,
    toPoint: finalPivot,
    distanceToNext: 0,
    cumulativeDistance: cumulativeDistance + finalDistance,
    angleChange: 0,
    label: labelForTurn("arrive", 0),
  });

  return merged.map((step, idx) => ({ ...step, index: idx }));
};

export const getActiveStepIndex = (
  userSvgPoint: { x: number; y: number },
  steps: NavigationStep[],
  lookAheadPx = 30
): number => {
  if (!steps.length) return 0;

  let nearestIndex = 0;
  let nearestDistance = Number.POSITIVE_INFINITY;

  steps.forEach((step, index) => {
    const d = distance(userSvgPoint, step.pivotPoint);
    if (d < nearestDistance) {
      nearestDistance = d;
      nearestIndex = index;
    }
  });

  if (nearestDistance <= lookAheadPx) return nearestIndex;

  return 0;
};
