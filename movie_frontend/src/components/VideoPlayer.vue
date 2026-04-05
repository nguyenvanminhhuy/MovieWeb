<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { SubtitleResponse, VideoSourceResponse } from '../api/types'
import { mediaUrl } from '../utils/media'

const props = defineProps<{
  sources: VideoSourceResponse[]
  subtitles?: SubtitleResponse[]
}>()

const emit = defineEmits<{
  timeupdate: [currentTime: number, duration: number]
  ended: []
}>()

const videoRef = ref<HTMLVideoElement | null>(null)
const iframeRef = ref<HTMLIFrameElement | null>(null)
const containerRef = ref<HTMLDivElement | null>(null)

const activeIndex = ref(0)
const mode = ref<'video' | 'embed'>('video')
const hlsCleanup = ref<(() => void) | null>(null)

const current = () => props.sources[activeIndex.value]

function resolvedUrl(url: string) {
  return mediaUrl(url)
}

async function attachHls(url: string, video: HTMLVideoElement) {
  const Hls = (await import('hls.js')).default
  if (Hls.isSupported()) {
    const hls = new Hls({ enableWorker: true })
    hls.loadSource(resolvedUrl(url))
    hls.attachMedia(video)
    return () => {
      hls.destroy()
    }
  }
  if (video.canPlayType('application/vnd.apple.mpegurl')) {
    video.src = resolvedUrl(url)
  }
  return () => {}
}

function setupPlayer() {
  hlsCleanup.value?.()
  hlsCleanup.value = null

  const src = current()
  if (!src?.url) {
    mode.value = 'embed'
    return
  }

  const t = src.type || 'MP4'
  if (t === 'EMBED') {
    mode.value = 'embed'
    return
  }

  mode.value = 'video'
  const video = videoRef.value
  if (!video) return

  video.pause()
  video.removeAttribute('src')
  video.load()

  if (t === 'HLS') {
    attachHls(src.url, video).then((fn) => {
      hlsCleanup.value = fn
    })
  } else {
    video.src = resolvedUrl(src.url)
  }
}

function onTimeUpdate() {
  const v = videoRef.value
  if (!v) return
  emit('timeupdate', v.currentTime, v.duration || 0)
}

function onEnded() {
  emit('ended')
}

watch(
  () => props.sources,
  () => {
    activeIndex.value = 0
    setupPlayer()
  },
  { deep: true },
)

watch(activeIndex, () => {
  setupPlayer()
})

onMounted(() => setupPlayer())

onBeforeUnmount(() => {
  hlsCleanup.value?.()
})

function selectQuality(i: number) {
  activeIndex.value = i
  setupPlayer()
}
</script>

<template>
  <div ref="containerRef" class="relative w-full overflow-hidden rounded-xl bg-black ring-1 ring-white/10">
    <template v-if="sources.length === 0">
      <div class="flex aspect-video items-center justify-center text-zinc-500">Chưa có nguồn phát</div>
    </template>

    <template v-else-if="mode === 'embed' && current()">
      <div class="aspect-video w-full">
        <iframe
          ref="iframeRef"
          class="h-full w-full"
          :src="resolvedUrl(current()!.url)"
          allowfullscreen
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        />
      </div>
    </template>

    <template v-else>
      <video
        ref="videoRef"
        class="aspect-video w-full bg-black"
        controls
        playsinline
        crossorigin="anonymous"
        @timeupdate="onTimeUpdate"
        @ended="onEnded"
      >
        <track
          v-for="(s, i) in subtitles || []"
          :key="s.id"
          kind="subtitles"
          :srclang="s.language || 'vi'"
          :label="s.language || 'Sub'"
          :src="resolvedUrl(s.url)"
          :default="i === 0"
        />
      </video>
    </template>

    <div
      v-if="sources.length > 1"
      class="absolute right-3 top-3 flex flex-wrap gap-1 rounded-lg bg-black/70 p-1 text-xs"
    >
      <button
        v-for="(s, i) in sources"
        :key="s.id"
        type="button"
        class="rounded px-2 py-1 font-medium transition"
        :class="
          i === activeIndex ? 'bg-violet-600 text-white' : 'text-zinc-300 hover:bg-white/10'
        "
        @click="selectQuality(i)"
      >
        {{ s.quality?.replace('P', '') || 'Auto' }}
      </button>
    </div>
  </div>
</template>
