import mitt from 'mitt'

type Events = {
  openLockScreen: void
  openSearch: void
}

export const mittBus = mitt<Events>()
