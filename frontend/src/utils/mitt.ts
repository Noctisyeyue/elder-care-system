import mitt from 'mitt'

type Events = {
  openLockScreen: void
  openChat: void
  openSearch: void
}

export const mittBus = mitt<Events>()
