/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_PREFIX?: string
  readonly VITE_REGISTER_ROLE_IDS?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
