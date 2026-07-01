import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { WorkTab } from '@/types/worktab'
import router from '@/router'

export const useWorktabStore = defineStore(
  'worktab',
  () => {
    const current = ref<Partial<WorkTab>>({})
    const opened = ref<WorkTab[]>([])

    const hasOpenedTabs = computed(() => opened.value.length > 0)

    function findTabIndex(path: string) {
      return opened.value.findIndex((tab) => tab.path === path)
    }

    function openTab(tab: WorkTab) {
      if (!tab.path) return
      const idx = findTabIndex(tab.path)
      if (idx === -1) {
        opened.value.push(tab)
      } else {
        opened.value[idx] = { ...opened.value[idx], ...tab }
      }
      current.value = tab
    }

    function setCurrentTab(path: string) {
      const tab = opened.value.find((t) => t.path === path)
      if (tab) current.value = tab
    }

    function removeTab(path: string, fallbackPath: string) {
      const idx = findTabIndex(path)
      if (idx === -1) return
      opened.value.splice(idx, 1)
      if (current.value.path === path) {
        const next = opened.value[idx] || opened.value[idx - 1]
        if (next) {
          router.push(next.path)
        } else {
          router.push(fallbackPath)
          openTab({ title: '首页', path: fallbackPath })
        }
      }
    }

    function removeAllTabs(fallbackPath: string, fallbackTitle = '首页') {
      opened.value = []
      current.value = {}
      router.push(fallbackPath)
      openTab({ title: fallbackTitle, path: fallbackPath })
    }

    function closeOtherTabs(path: string) {
      opened.value = opened.value.filter((t) => t.path === path || t.fixedTab)
      setCurrentTab(path)
    }

    function clearTabs() {
      opened.value = []
      current.value = {}
    }

    return {
      current,
      opened,
      hasOpenedTabs,
      openTab,
      setCurrentTab,
      removeTab,
      removeAllTabs,
      closeOtherTabs,
      clearTabs,
      findTabIndex,
    }
  },
  {
    persist: {
      key: 'elder-care-worktab',
      pick: ['opened'],
    },
  },
)
