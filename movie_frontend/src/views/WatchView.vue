<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import * as api from '../api/animeApi'
import type { CommentResponse, EpisodeResponse, MovieResponse, ReviewResponse } from '../api/types'
import type { VideoSourceResponse, SubtitleResponse } from '../api/types'
import AppShell from '../components/AppShell.vue'
import VideoPlayer from '../components/VideoPlayer.vue'
import CommentThread from '../components/CommentThread.vue'
import { useAuth } from '../composables/useAuth'
import { ApiError } from '../api/client'

const route = useRoute()
const { isLoggedIn } = useAuth()

const movie = ref<MovieResponse | null>(null)
const episodes = ref<EpisodeResponse[]>([])
const selectedId = ref<string | null>(null)
const sources = ref<VideoSourceResponse[]>([])
const subtitles = ref<SubtitleResponse[]>([])
const comments = ref<CommentResponse[]>([])
const reviews = ref<ReviewResponse[]>([])
const loading = ref(true)
const err = ref('')

const commentText = ref('')
const replyTo = ref<string | null>(null)
const reviewRating = ref(8)
const reviewContent = ref('')
const actionMsg = ref('')

const movieId = computed(() => route.params.movieId as string)

const selectedEpisode = computed(() =>
  episodes.value.find((e) => e.id === selectedId.value),
)

let historyTimer: ReturnType<typeof setTimeout> | null = null

function scheduleHistorySave(currentTime: number) {
  if (!isLoggedIn.value || !selectedId.value) return
  if (historyTimer) clearTimeout(historyTimer)
  historyTimer = setTimeout(() => {
    void api.saveWatchProgress(selectedId.value!, Math.floor(currentTime))
  }, 10000)
}

async function loadEpisodeMedia(ep: EpisodeResponse) {
  let v = ep.videoSources ?? []
  let s = ep.subtitles ?? []
  if (!v.length) {
    try {
      v = await api.getVideoSources(ep.id)
    } catch {
      v = []
    }
  }
  if (!s.length) {
    try {
      s = await api.getSubtitles(ep.id)
    } catch {
      s = []
    }
  }
  sources.value = v
  subtitles.value = s
}

async function selectEpisode(ep: EpisodeResponse) {
  selectedId.value = ep.id
  await loadEpisodeMedia(ep)
}

async function load() {
  loading.value = true
  err.value = ''
  try {
    const id = movieId.value
    const [m, eps, cm, rv] = await Promise.all([
      api.getMovie(id),
      api.getEpisodes(id),
      api.getComments(id),
      api.getReviews(id),
    ])
    movie.value = m
    episodes.value = [...eps].sort((a, b) => a.episodeNumber - b.episodeNumber)
    comments.value = cm
    reviews.value = rv

    const qEp = route.query.episode as string | undefined
    let first = episodes.value[0]
    if (qEp) {
      const found = episodes.value.find((e) => e.id === qEp)
      if (found) first = found
    }
    if (first) await selectEpisode(first)
    else {
      sources.value = []
      subtitles.value = []
    }
    if (isLoggedIn.value) {
      try {
        const fav = await api.getFavorites(1, 200)
        favorited.value = fav.data.some((m) => m.id === id)
      } catch {
        favorited.value = false
      }
    }
  } catch (e) {
    err.value = e instanceof Error ? e.message : 'Không tải được'
  } finally {
    loading.value = false
  }
}

onMounted(() => void load())

watch(movieId, () => void load())

watch(
  () => route.query.episode,
  async (ep) => {
    if (typeof ep === 'string' && ep && episodes.value.length) {
      const found = episodes.value.find((e) => e.id === ep)
      if (found) await selectEpisode(found)
    }
  },
)

async function submitComment() {
  if (!isLoggedIn.value || !movie.value || !commentText.value.trim()) return
  actionMsg.value = ''
  try {
    await api.postComment(movie.value.id, commentText.value.trim(), replyTo.value ?? undefined)
    commentText.value = ''
    replyTo.value = null
    comments.value = await api.getComments(movie.value.id)
  } catch (e) {
    actionMsg.value = e instanceof ApiError ? e.message : 'Không gửi được bình luận'
  }
}

async function onReply(parentId: string) {
  replyTo.value = parentId
  document.getElementById('comment-box')?.scrollIntoView({ behavior: 'smooth' })
}

async function onLikeComment(id: string) {
  if (!isLoggedIn.value) return
  try {
    await api.likeComment(id)
    if (movie.value) comments.value = await api.getComments(movie.value.id)
  } catch {
    /* ignore */
  }
}

async function submitReview() {
  if (!isLoggedIn.value || !movie.value) return
  actionMsg.value = ''
  try {
    await api.postReview(movie.value.id, reviewRating.value, reviewContent.value.trim())
    reviewContent.value = ''
    reviews.value = await api.getReviews(movie.value.id)
  } catch (e) {
    actionMsg.value = e instanceof ApiError ? e.message : 'Không gửi được đánh giá'
  }
}

const favorited = ref(false)

async function toggleFavorite() {
  if (!isLoggedIn.value || !movie.value) return
  try {
    if (favorited.value) {
      await api.removeFavorite(movie.value.id)
      favorited.value = false
    } else {
      await api.addFavorite(movie.value.id)
      favorited.value = true
    }
  } catch {
    /* ignore */
  }
}

</script>

<template>
  <AppShell>
    <div v-if="loading" class="py-24 text-center text-zinc-500">Đang tải…</div>
    <div v-else-if="err || !movie" class="text-red-400">{{ err || 'Lỗi' }}</div>
    <div v-else class="space-y-10">
      <div class="flex flex-col gap-4 lg:flex-row lg:items-start">
        <div class="min-w-0 flex-1 space-y-4">
          <div class="flex flex-wrap items-center gap-3">
            <RouterLink
              :to="{ name: 'movie', params: { id: movie.id } }"
              class="text-sm text-violet-400 hover:underline"
            >
              ← {{ movie.title }}
            </RouterLink>
            <button
              v-if="isLoggedIn"
              type="button"
              class="rounded-full border border-white/15 px-4 py-1.5 text-sm hover:bg-white/10"
              @click="toggleFavorite"
            >
              {{ favorited ? '♥ Đã thích' : '♡ Yêu thích' }}
            </button>
          </div>

          <VideoPlayer
            :sources="sources"
            :subtitles="subtitles"
            @timeupdate="(ct) => scheduleHistorySave(ct)"
          />

          <p v-if="selectedEpisode" class="text-sm text-zinc-500">
            Đang phát: Tập {{ selectedEpisode.episodeNumber
            }}{{ selectedEpisode.title ? ` — ${selectedEpisode.title}` : '' }}
          </p>
        </div>

        <aside
          class="w-full shrink-0 rounded-xl bg-zinc-900/60 p-4 ring-1 ring-white/10 lg:w-80 xl:w-96"
        >
          <h3 class="font-semibold text-zinc-200">Danh sách tập</h3>
          <ul class="mt-3 max-h-[480px] space-y-1 overflow-y-auto pr-1">
            <li v-for="ep in episodes" :key="ep.id">
              <button
                type="button"
                class="flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-sm transition"
                :class="
                  ep.id === selectedId ? 'bg-violet-600/40 text-white' : 'hover:bg-white/10'
                "
                @click="selectEpisode(ep)"
              >
                <span>Tập {{ ep.episodeNumber }}</span>
                <span class="text-xs text-zinc-500">{{ ep.duration ? `${ep.duration}′` : '' }}</span>
              </button>
            </li>
          </ul>
          <p v-if="!episodes.length" class="mt-2 text-sm text-zinc-500">Chưa có tập nào.</p>
        </aside>
      </div>

      <section class="grid gap-10 lg:grid-cols-2">
        <div>
          <h3 class="text-lg font-bold">Bình luận</h3>
          <div v-if="isLoggedIn" id="comment-box" class="mt-4 space-y-2">
            <p v-if="replyTo" class="text-xs text-violet-400">Trả lời bình luận — <button type="button" class="underline" @click="replyTo = null">Hủy</button></p>
            <textarea
              v-model="commentText"
              rows="3"
              class="w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-violet-500"
              placeholder="Viết bình luận..."
            />
            <button
              type="button"
              class="rounded-lg bg-violet-600 px-4 py-2 text-sm font-medium hover:bg-violet-500"
              @click="submitComment"
            >
              Gửi
            </button>
          </div>
          <p v-else class="mt-4 text-sm text-zinc-500">
            <RouterLink to="/login" class="text-violet-400">Đăng nhập</RouterLink> để bình luận.
          </p>
          <CommentThread
            class="mt-6"
            :comments="comments"
            @reply="onReply"
            @like="onLikeComment"
          />
        </div>

        <div>
          <h3 class="text-lg font-bold">Đánh giá</h3>
          <div v-if="isLoggedIn" class="mt-4 space-y-2">
            <label class="flex items-center gap-2 text-sm">
              <span class="text-zinc-500">Điểm (1–10)</span>
              <input
                v-model.number="reviewRating"
                type="number"
                min="1"
                max="10"
                class="w-20 rounded border border-white/10 bg-zinc-950 px-2 py-1"
              />
            </label>
            <textarea
              v-model="reviewContent"
              rows="3"
              class="w-full rounded-lg border border-white/10 bg-zinc-950 px-3 py-2 text-sm"
              placeholder="Nhận xét (tùy chọn)"
            />
            <button
              type="button"
              class="rounded-lg bg-violet-600 px-4 py-2 text-sm font-medium"
              @click="submitReview"
            >
              Gửi đánh giá
            </button>
          </div>
          <p v-else class="mt-4 text-sm text-zinc-500">
            <RouterLink to="/login" class="text-violet-400">Đăng nhập</RouterLink> để đánh giá.
          </p>
          <ul class="mt-6 space-y-4">
            <li
              v-for="r in reviews"
              :key="r.id"
              class="rounded-lg bg-zinc-900/50 p-4 ring-1 ring-white/5"
            >
              <div class="flex items-center justify-between">
                <span class="font-medium text-violet-300">{{
                  r.user?.fullName || r.user?.username || 'Ẩn danh'
                }}</span>
                <span class="text-amber-400">★ {{ r.rating }}/10</span>
              </div>
              <p class="mt-2 text-sm text-zinc-300">{{ r.content }}</p>
              <p class="mt-2 text-xs text-zinc-600">
                {{ r.createdAt ? new Date(r.createdAt).toLocaleString('vi-VN') : '' }}
              </p>
            </li>
          </ul>
          <p v-if="!reviews.length" class="mt-4 text-sm text-zinc-500">Chưa có đánh giá.</p>
        </div>
      </section>

      <p v-if="actionMsg" class="text-sm text-amber-400">{{ actionMsg }}</p>
    </div>
  </AppShell>
</template>
