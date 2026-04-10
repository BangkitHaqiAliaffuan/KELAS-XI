import * as THREE from 'three';

export class FirstPersonControls {
  constructor(camera, domElement) {
    this.camera = camera;
    this.domElement = domElement;

    this.speed = 5;
    this.mouseSensitivity = 0.002;
    this.dampingFactor = 0.1;

    this.player = new THREE.Object3D();
    this.player.position.y = 1.7;
    this.player.add(this.camera);

    this.camera.position.set(0, 0, 0);
    this.camera.rotation.set(0, 0, 0);

    this.pitch = 0;
    this.minPitch = THREE.MathUtils.degToRad(-80);
    this.maxPitch = THREE.MathUtils.degToRad(80);

    this.moveInput = new THREE.Vector3();
    this.currentVelocity = new THREE.Vector3();
    this.targetVelocity = new THREE.Vector3();

    this.isLocked = false;
    this.keys = {
      KeyW: false,
      KeyA: false,
      KeyS: false,
      KeyD: false
    };

    this.overlay = document.createElement('div');
    this.overlay.textContent = 'Click to start';
    Object.assign(this.overlay.style, {
      position: 'fixed',
      inset: '0',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'rgba(0, 0, 0, 0.5)',
      color: '#ffffff',
      fontSize: '28px',
      userSelect: 'none',
      cursor: 'pointer',
      zIndex: '1000'
    });
    document.body.appendChild(this.overlay);

    this.onKeyDown = this.handleKeyDown.bind(this);
    this.onKeyUp = this.handleKeyUp.bind(this);
    this.onMouseMove = this.handleMouseMove.bind(this);
    this.onPointerLockChange = this.handlePointerLockChange.bind(this);
    this.onOverlayClick = this.lockPointer.bind(this);

    document.addEventListener('keydown', this.onKeyDown);
    document.addEventListener('keyup', this.onKeyUp);
    document.addEventListener('mousemove', this.onMouseMove);
    document.addEventListener('pointerlockchange', this.onPointerLockChange);
    this.overlay.addEventListener('click', this.onOverlayClick);
    this.domElement.addEventListener('click', this.onOverlayClick);
  }

  handleKeyDown(event) {
    if (event.code in this.keys) {
      this.keys[event.code] = true;
    }
  }

  handleKeyUp(event) {
    if (event.code in this.keys) {
      this.keys[event.code] = false;
    }
  }

  handleMouseMove(event) {
    if (!this.isLocked) {
      return;
    }

    this.player.rotation.y -= event.movementX * this.mouseSensitivity;
    this.pitch -= event.movementY * this.mouseSensitivity;
    this.pitch = THREE.MathUtils.clamp(this.pitch, this.minPitch, this.maxPitch);
    this.camera.rotation.x = this.pitch;
  }

  handlePointerLockChange() {
    this.isLocked = document.pointerLockElement === this.domElement;
    this.overlay.style.display = this.isLocked ? 'none' : 'flex';
  }

  lockPointer() {
    if (!this.isLocked) {
      this.domElement.requestPointerLock();
    }
  }

  update(deltaTime) {
    this.moveInput.set(0, 0, 0);

    if (this.keys.KeyW) {
      this.moveInput.z += 1;
    }
    if (this.keys.KeyS) {
      this.moveInput.z -= 1;
    }
    if (this.keys.KeyA) {
      this.moveInput.x -= 1;
    }
    if (this.keys.KeyD) {
      this.moveInput.x += 1;
    }

    if (this.moveInput.lengthSq() > 0) {
      this.moveInput.normalize();
    }

    this.targetVelocity.copy(this.moveInput).multiplyScalar(this.speed);
    this.currentVelocity.lerp(this.targetVelocity, this.dampingFactor);

    const forward = new THREE.Vector3();
    this.camera.getWorldDirection(forward);
    const right = new THREE.Vector3().crossVectors(forward, this.camera.up);

    forward.y = 0;
    right.y = 0;
    forward.normalize();
    right.normalize();

    const movement = new THREE.Vector3();
    movement.addScaledVector(forward, this.currentVelocity.z * deltaTime);
    movement.addScaledVector(right, this.currentVelocity.x * deltaTime);

    this.player.position.add(movement);
    this.player.position.y = 1.7;
  }

  dispose() {
    document.removeEventListener('keydown', this.onKeyDown);
    document.removeEventListener('keyup', this.onKeyUp);
    document.removeEventListener('mousemove', this.onMouseMove);
    document.removeEventListener('pointerlockchange', this.onPointerLockChange);
    this.overlay.removeEventListener('click', this.onOverlayClick);
    this.domElement.removeEventListener('click', this.onOverlayClick);

    if (document.pointerLockElement === this.domElement) {
      document.exitPointerLock();
    }

    this.overlay.remove();
  }
}
