import * as faceapi from '@vladmandic/face-api'

const MODEL_URL = '/face-api/model'
const MATCH_THRESHOLD = 0.6
let modelsLoaded = false
let loadingPromise = null

function getStorageKey(userId) {
  return `faceDescriptor_${userId}`
}

export function getStoredFaceDescriptor(userId) {
  if (!userId) return null
  const raw = localStorage.getItem(getStorageKey(userId))
  if (!raw) return null
  try {
    return new Float32Array(JSON.parse(raw))
  } catch {
    return null
  }
}

export function saveFaceDescriptor(userId, descriptor) {
  if (!userId || !descriptor) return
  localStorage.setItem(getStorageKey(userId), JSON.stringify(Array.from(descriptor)))
}

export async function loadFaceModels() {
  if (modelsLoaded) return true
  if (loadingPromise) return loadingPromise

  loadingPromise = (async () => {
    await Promise.all([
      faceapi.nets.tinyFaceDetector.loadFromUri(MODEL_URL),
      faceapi.nets.faceLandmark68TinyNet.loadFromUri(MODEL_URL),
      faceapi.nets.faceRecognitionNet.loadFromUri(MODEL_URL)
    ])
    modelsLoaded = true
    return true
  })()

  return loadingPromise
}

async function detectFaceFromVideo(videoEl) {
  await loadFaceModels()
  const options = new faceapi.TinyFaceDetectorOptions({ inputSize: 320, scoreThreshold: 0.5 })
  return faceapi
    .detectSingleFace(videoEl, options)
    .withFaceLandmarks(true)
    .withFaceDescriptor()
}

export async function verifyFace(videoEl, userId) {
  const detection = await detectFaceFromVideo(videoEl)
  if (!detection) {
    return { success: false, message: '未检测到人脸，请正对摄像头后重试' }
  }

  const storedDescriptor = getStoredFaceDescriptor(userId)
  if (!storedDescriptor) {
    saveFaceDescriptor(userId, detection.descriptor)
    return { success: true, message: '首次核验成功，已录入人脸信息', enrolled: true }
  }

  const distance = faceapi.euclideanDistance(storedDescriptor, detection.descriptor)
  if (distance <= MATCH_THRESHOLD) {
    return { success: true, message: '人脸核验成功', distance }
  }

  return {
    success: false,
    message: `人脸不匹配，请确认本人操作（相似度 ${(Math.max(0, 1 - distance) * 100).toFixed(0)}%）`
  }
}

export async function enrollFace(videoEl, userId) {
  const detection = await detectFaceFromVideo(videoEl)
  if (!detection) {
    return { success: false, message: '未检测到人脸，请正对摄像头' }
  }
  saveFaceDescriptor(userId, detection.descriptor)
  return { success: true, message: '人脸录入成功' }
}

export function stopCameraStream(videoEl) {
  const stream = videoEl?.srcObject
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    videoEl.srcObject = null
  }
}
