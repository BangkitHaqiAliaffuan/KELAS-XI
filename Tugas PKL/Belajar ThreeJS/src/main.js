import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { FirstPersonControls } from './FirstPersonControls.js';

const scene = new THREE.Scene();
scene.background = new THREE.Color(0x9dc4e8);

const camera = new THREE.PerspectiveCamera(
  75,
  window.innerWidth / window.innerHeight,
  0.1,
  1000
);

const renderer = new THREE.WebGLRenderer({ antialias: true });
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

const controls = new OrbitControls(camera, renderer.domElement);
controls.enableDamping = true;
controls.dampingFactor = 0.08;
controls.minDistance = 3;
controls.maxDistance = 600;
controls.enableRotate = false;
controls.enablePan = true;
controls.screenSpacePanning = true;
controls.mouseButtons.LEFT = THREE.MOUSE.PAN;
controls.mouseButtons.RIGHT = THREE.MOUSE.PAN;
controls.maxPolarAngle = Math.PI / 2.05;

const ambientLight = new THREE.AmbientLight(0xffffff, 0.7);
scene.add(ambientLight);

const sunLight = new THREE.DirectionalLight(0xffffff, 0.8);
sunLight.position.set(10, 20, 10);
scene.add(sunLight);

const ground = new THREE.Mesh(
  new THREE.PlaneGeometry(200, 200),
  new THREE.MeshStandardMaterial({ color: 0x606060 })
);
ground.rotation.x = -Math.PI / 2;
ground.position.y = 0;
scene.add(ground);

const loader = new GLTFLoader();
const cityRoot = new THREE.Group();
scene.add(cityRoot);

const apartmentRoot = new THREE.Group();
apartmentRoot.visible = false;
scene.add(apartmentRoot);

const raycaster = new THREE.Raycaster();
const pointer = new THREE.Vector2();
const clickableObjects = [];

let cityModel = null;
let apartmentModel = null;
let exclamationMarker = null;
let isApartmentView = false;
let firstPersonControls = null;
let cityRotateDirection = 0;
let cityHorizontalDistance = 0;
let cityZoomLevel = 1;

const backButton = document.createElement('button');
backButton.textContent = 'Back to City';
Object.assign(backButton.style, {
  position: 'fixed',
  top: '16px',
  left: '16px',
  padding: '10px 14px',
  border: 'none',
  borderRadius: '8px',
  background: '#1f2937',
  color: '#ffffff',
  fontSize: '14px',
  fontWeight: 'bold',
  cursor: 'pointer',
  boxShadow: '0 4px 12px rgba(0,0,0,0.35)',
  zIndex: '2000',
  display: 'none'
});
document.body.appendChild(backButton);

const rotateLeftButton = document.createElement('button');
rotateLeftButton.textContent = '⟲ Rotate Left';
Object.assign(rotateLeftButton.style, {
  position: 'fixed',
  top: '16px',
  left: '150px',
  padding: '10px 14px',
  border: 'none',
  borderRadius: '8px',
  background: '#1f2937',
  color: '#ffffff',
  fontSize: '14px',
  fontWeight: 'bold',
  cursor: 'pointer',
  boxShadow: '0 4px 12px rgba(0,0,0,0.35)',
  zIndex: '2000',
  display: 'block'
});
document.body.appendChild(rotateLeftButton);

const rotateRightButton = document.createElement('button');
rotateRightButton.textContent = 'Rotate Right ⟳';
Object.assign(rotateRightButton.style, {
  position: 'fixed',
  top: '16px',
  left: '300px',
  padding: '10px 14px',
  border: 'none',
  borderRadius: '8px',
  background: '#1f2937',
  color: '#ffffff',
  fontSize: '14px',
  fontWeight: 'bold',
  cursor: 'pointer',
  boxShadow: '0 4px 12px rgba(0,0,0,0.35)',
  zIndex: '2000',
  display: 'block'
});
document.body.appendChild(rotateRightButton);

const cityPath = '/assets/city_orbit/source/Project.glb';
const apartmentPath = '/assets/Apartment/Apartment%202.glb';

const APARTMENT_CONFIG = {
  scale: 14,
  eyeHeight: 1.45,
  positionX: 0,
  positionY: 1,
  firstPersonSpawnOffsetX: -4
};

const CITY_VIEW_CONFIG = {
  fov: 35,
  distance: 260,
  minDistance: 120,
  maxDistance: 700,
  minZoom: 0.7,
  maxZoom: 2.4,
  zoomStep: 0.15,
  defaultZoom: 1,
  cameraY: 400,
  targetY: 0,
  autoRotateSpeed: THREE.MathUtils.degToRad(22),
  polarAngle: THREE.MathUtils.degToRad(55),
  azimuthAngle: THREE.MathUtils.degToRad(45),
  panSpeed: 1.1
};

const MARKER_CONFIG = {
  size: 15,
  offsetY: 50,
  offsetX: 100,
  offsetZ: 20
};

function createExclamationTexture() {
  const canvas = document.createElement('canvas');
  canvas.width = 128;
  canvas.height = 128;
  const context = canvas.getContext('2d');

  context.clearRect(0, 0, 128, 128);
  context.beginPath();
  context.arc(64, 64, 54, 0, Math.PI * 2);
  context.fillStyle = '#ffd54a';
  context.fill();
  context.lineWidth = 6;
  context.strokeStyle = '#1e1e1e';
  context.stroke();

  context.fillStyle = '#1e1e1e';
  context.font = 'bold 78px Arial';
  context.textAlign = 'center';
  context.textBaseline = 'middle';
  context.fillText('!', 64, 70);

  const texture = new THREE.CanvasTexture(canvas);
  texture.colorSpace = THREE.SRGBColorSpace;
  return texture;
}

function findFirstMesh(root) {
  let firstMesh = null;
  root.traverse((child) => {
    if (!firstMesh && child.isMesh) {
      firstMesh = child;
    }
  });
  return firstMesh;
}

function applyCityIsometricSettings() {
  camera.fov = CITY_VIEW_CONFIG.fov;
  cityZoomLevel = THREE.MathUtils.clamp(
    cityZoomLevel || CITY_VIEW_CONFIG.defaultZoom,
    CITY_VIEW_CONFIG.minZoom,
    CITY_VIEW_CONFIG.maxZoom
  );
  camera.zoom = cityZoomLevel;
  camera.updateProjectionMatrix();

  controls.enableRotate = false;
  controls.enablePan = true;
  controls.enableZoom = false;
  controls.panSpeed = CITY_VIEW_CONFIG.panSpeed;
  controls.mouseButtons.LEFT = THREE.MOUSE.PAN;
  controls.mouseButtons.RIGHT = THREE.MOUSE.PAN;
  controls.minPolarAngle = CITY_VIEW_CONFIG.polarAngle;
  controls.maxPolarAngle = CITY_VIEW_CONFIG.polarAngle;
  controls.minAzimuthAngle = -Infinity;
  controls.maxAzimuthAngle = Infinity;
}

function lockCityIsometricY() {
  controls.target.y = CITY_VIEW_CONFIG.targetY;
  camera.position.y = CITY_VIEW_CONFIG.cameraY;
}

function updateRotateButtonsState() {
  rotateLeftButton.style.background = cityRotateDirection === -1 ? '#0f766e' : '#1f2937';
  rotateRightButton.style.background = cityRotateDirection === 1 ? '#0f766e' : '#1f2937';
}

updateRotateButtonsState();

function frameOrbitToObject(object, offset = new THREE.Vector3(14, 10, 14)) {
  const bounds = new THREE.Box3().setFromObject(object);
  const center = bounds.getCenter(new THREE.Vector3());

  controls.target.copy(center);
  if (offset === null) {
    controls.target.y = CITY_VIEW_CONFIG.targetY;
    cityHorizontalDistance = THREE.MathUtils.clamp(
      CITY_VIEW_CONFIG.distance,
      CITY_VIEW_CONFIG.minDistance,
      CITY_VIEW_CONFIG.maxDistance
    );
    const isometricOffset = new THREE.Vector3().setFromSpherical(
      new THREE.Spherical(
        cityHorizontalDistance,
        CITY_VIEW_CONFIG.polarAngle,
        CITY_VIEW_CONFIG.azimuthAngle
      )
    );
    camera.position.copy(controls.target).add(isometricOffset);
    camera.position.y = CITY_VIEW_CONFIG.cameraY;
  } else {
    camera.position.copy(center).add(offset);
  }
  controls.update();
}

function applyCityZoom(stepDirection) {
  if (isApartmentView) {
    return;
  }

  const nextZoom = THREE.MathUtils.clamp(
    cityZoomLevel + -stepDirection * CITY_VIEW_CONFIG.zoomStep,
    CITY_VIEW_CONFIG.minZoom,
    CITY_VIEW_CONFIG.maxZoom
  );

  cityZoomLevel = nextZoom;
  camera.zoom = cityZoomLevel;
  camera.updateProjectionMatrix();
}

function setupApartmentFirstPerson() {
  if (!apartmentModel) {
    return;
  }

  if (!firstPersonControls) {
    firstPersonControls = new FirstPersonControls(camera, renderer.domElement);
    firstPersonControls.speed = 4;
    firstPersonControls.mouseSensitivity = 0.002;
    firstPersonControls.player.position.y = APARTMENT_CONFIG.eyeHeight;
    scene.add(firstPersonControls.player);
  }

  const bounds = new THREE.Box3().setFromObject(apartmentModel);
  const center = bounds.getCenter(new THREE.Vector3());
  const depth = Math.max(bounds.getSize(new THREE.Vector3()).z, 2);

  const spawn = new THREE.Vector3(
    center.x + APARTMENT_CONFIG.firstPersonSpawnOffsetX,
    bounds.min.y + APARTMENT_CONFIG.eyeHeight,
    center.z + depth * 0.25
  );
  firstPersonControls.player.position.copy(spawn);

  const lookDirection = new THREE.Vector3(center.x - spawn.x, 0, center.z - spawn.z);
  if (lookDirection.lengthSq() > 0) {
    lookDirection.normalize();
    firstPersonControls.player.rotation.y = Math.atan2(lookDirection.x, -lookDirection.z);
  }

  firstPersonControls.pitch = 0;
  firstPersonControls.camera.rotation.set(0, 0, 0);
  firstPersonControls.currentVelocity.set(0, 0, 0);
  firstPersonControls.targetVelocity.set(0, 0, 0);
  firstPersonControls.moveInput.set(0, 0, 0);
}

function teardownApartmentFirstPerson() {
  if (!firstPersonControls) {
    return;
  }

  const worldPosition = new THREE.Vector3();
  const worldQuaternion = new THREE.Quaternion();
  camera.getWorldPosition(worldPosition);
  camera.getWorldQuaternion(worldQuaternion);

  firstPersonControls.player.remove(camera);
  scene.add(camera);
  camera.position.copy(worldPosition);
  camera.quaternion.copy(worldQuaternion);

  firstPersonControls.dispose();
  if (firstPersonControls.player.parent) {
    firstPersonControls.player.parent.remove(firstPersonControls.player);
  }
  firstPersonControls = null;
}

function switchToApartmentView() {
  if (!apartmentModel || isApartmentView) {
    return;
  }

  camera.fov = 75;
  camera.zoom = 1;
  camera.updateProjectionMatrix();
  cityRotateDirection = 0;
  updateRotateButtonsState();
  isApartmentView = true;
  controls.enabled = false;
  cityRoot.visible = false;
  apartmentRoot.visible = true;
  backButton.style.display = 'block';
  rotateLeftButton.style.display = 'none';
  rotateRightButton.style.display = 'none';
  setupApartmentFirstPerson();
}

function switchToCityView() {
  if (!cityModel || !isApartmentView) {
    return;
  }

  teardownApartmentFirstPerson();
  isApartmentView = false;
  controls.enabled = true;
  apartmentRoot.visible = false;
  cityRoot.visible = true;
  backButton.style.display = 'none';
  rotateLeftButton.style.display = 'block';
  rotateRightButton.style.display = 'block';
  applyCityIsometricSettings();
  frameOrbitToObject(cityModel, null);
}

function setupMarkerOnCity() {
  if (!cityModel) {
    return;
  }

  const targetBuilding = findFirstMesh(cityModel);
  if (!targetBuilding) {
    return;
  }

  const buildingBounds = new THREE.Box3().setFromObject(targetBuilding);
  const center = buildingBounds.getCenter(new THREE.Vector3());
  const markerX = center.x + MARKER_CONFIG.offsetX;
  const markerY = buildingBounds.max.y + MARKER_CONFIG.offsetY;
  const markerZ = center.z + MARKER_CONFIG.offsetZ;

  const markerTexture = createExclamationTexture();
  const markerMaterial = new THREE.SpriteMaterial({ map: markerTexture, depthTest: false });
  exclamationMarker = new THREE.Sprite(markerMaterial);
  exclamationMarker.position.set(markerX, markerY, markerZ);
  exclamationMarker.scale.setScalar(MARKER_CONFIG.size);
  exclamationMarker.userData.type = 'apartment-switch';

  cityRoot.add(exclamationMarker);
  clickableObjects.push(exclamationMarker);
}

loader.load(
  cityPath,
  (gltf) => {
    cityModel = gltf.scene;
    cityRoot.add(cityModel);
    setupMarkerOnCity();
    applyCityIsometricSettings();
    frameOrbitToObject(cityModel, null);
  },
  undefined,
  (error) => {
    console.error('Failed to load city_orbit model', error);
  }
);

loader.load(
  apartmentPath,
  (gltf) => {
    apartmentModel = gltf.scene;
    apartmentModel.scale.setScalar(APARTMENT_CONFIG.scale);
    apartmentRoot.add(apartmentModel);
    apartmentRoot.position.set(APARTMENT_CONFIG.positionX, APARTMENT_CONFIG.positionY, 0);
  },
  undefined,
  (error) => {
    console.error('Failed to load Apartment model', error);
  }
);

renderer.domElement.addEventListener('pointerdown', (event) => {
  if (isApartmentView) {
    return;
  }

  const rect = renderer.domElement.getBoundingClientRect();
  pointer.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
  pointer.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

  raycaster.setFromCamera(pointer, camera);
  const intersects = raycaster.intersectObjects(clickableObjects, false);

  if (intersects.length > 0 && intersects[0].object.userData.type === 'apartment-switch') {
    switchToApartmentView();
  }
});

backButton.addEventListener('click', () => {
  switchToCityView();
});

rotateLeftButton.addEventListener('click', () => {
  if (isApartmentView) {
    return;
  }

  cityRotateDirection = cityRotateDirection === -1 ? 0 : -1;
  updateRotateButtonsState();
});

rotateRightButton.addEventListener('click', () => {
  if (isApartmentView) {
    return;
  }

  cityRotateDirection = cityRotateDirection === 1 ? 0 : 1;
  updateRotateButtonsState();
});

renderer.domElement.addEventListener(
  'wheel',
  (event) => {
    if (isApartmentView) {
      return;
    }

    event.preventDefault();
    if (event.deltaY < 0) {
      applyCityZoom(-1);
    } else if (event.deltaY > 0) {
      applyCityZoom(1);
    }
  },
  { passive: false }
);

const clock = new THREE.Clock();

function animate() {
  requestAnimationFrame(animate);
  const deltaTime = clock.getDelta();

  if (isApartmentView && firstPersonControls) {
    firstPersonControls.update(deltaTime);
  } else {
    if (cityRotateDirection !== 0) {
      if (typeof controls.rotateLeft === 'function') {
        controls.rotateLeft(-cityRotateDirection * CITY_VIEW_CONFIG.autoRotateSpeed * deltaTime);
      } else {
        const theta = Math.atan2(
          camera.position.x - controls.target.x,
          camera.position.z - controls.target.z
        );
        const radius = Math.hypot(
          camera.position.x - controls.target.x,
          camera.position.z - controls.target.z
        );
        const nextTheta = theta + cityRotateDirection * CITY_VIEW_CONFIG.autoRotateSpeed * deltaTime;

        camera.position.x = controls.target.x + Math.sin(nextTheta) * radius;
        camera.position.z = controls.target.z + Math.cos(nextTheta) * radius;
      }
    }
    controls.update();
    lockCityIsometricY();
  }

  if (exclamationMarker) {
    exclamationMarker.lookAt(camera.position);
  }

  renderer.render(scene, camera);
}

animate();

window.addEventListener('resize', () => {
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(window.innerWidth, window.innerHeight);
});
