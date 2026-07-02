<!-- 管理端--子菜单--床位示意图 -->
<template>
  <div class="bed-map-page">
    <!-- 楼层选择和统计 -->
    <div class="bed-map-toolbar">
      <div class="toolbar-left">
        <el-select v-model="selectedBuilding" @change="onBuildingChange" style="width: 120px" placeholder="选择楼栋">
          <el-option v-for="item in buildingOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="selectedFloor" @change="fetchRoomList" style="width: 120px">
          <el-option v-for="floor in floorList" :key="floor" :label="floor" :value="floor" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <span class="stat-item stat-total">
          <img :src="totalBedSvg" alt="总床位" class="icon-bed" />
          总床位：{{ bedStats.total }}
        </span>
        <span class="stat-item stat-free">
          <img :src="freeBedSvg" alt="空闲" class="icon-bed" />
          空闲：{{ bedStats.free }}
        </span>
        <span class="stat-item stat-used">
          <img :src="usedBedSvg" alt="有人" class="icon-bed" />
          有人：{{ bedStats.used }}
        </span>
        <span class="stat-item stat-out">
          <img :src="outBedSvg" alt="外出" class="icon-bed" />
          外出：{{ bedStats.out }}
        </span>
      </div>
    </div>

    <!-- 房间床位卡片网格 -->
    <div class="room-grid-wrapper">
      <div class="floor-shell">
        <!-- 西门：整层最左侧竖条 -->
        <div class="gate-side gate-west">
          <span class="gate-label-vertical">西门</span>
        </div>

        <div class="floor-layout" :style="floorLayoutStyle">
          <template v-for="(row, rowIdx) in roomRows" :key="`row-${rowIdx}`">
            <div class="room-grid" :style="getRoomGridStyle(row)">
              <div v-for="room in row" :key="room.roomNumber" class="room-card">
              <div class="room-title">{{ room.roomNumber }}</div>
              <div class="beds-wrapper">
                <div class="bed-row">
                  <template v-for="(bed, bedIdx) in room.beds.slice(0, 2)" :key="bed.bedNo">
                    <div :class="['bed-status-container', `bed-${bed.status}`]">
                      <el-tooltip placement="top" :effect="tooltipEffect" popper-class="bed-tooltip">
                        <template #content>
                          <div v-if="room.details && room.details[bedIdx]" class="customer-info-card">
                            <div class="customer-info-title">客户信息</div>
                            <div class="info-row"><span class="label">姓名：</span><span class="value">{{ room.details[bedIdx].customerName }}</span></div>
                            <div class="info-row"><span class="label">性别：</span><span class="value">{{ room.details[bedIdx].gender }}</span></div>
                            <div class="info-row"><span class="label">年龄：</span><span class="value">{{ room.details[bedIdx].age }}</span></div>
                            <div class="info-row"><span class="label">入住时间：</span><span class="value">{{ room.details[bedIdx].usageStartDate }}</span></div>
                            <div class="info-row"><span class="label">到期时间：</span><span class="value">{{ room.details[bedIdx].usageEndDate }}</span></div>
                          </div>
                          <div v-else class="status-text">
                            {{ bedStatusText(bed.status) }}
                          </div>
                        </template>
                        <img :src="getBedIcon(bed.status)" class="bed-icon" />
                      </el-tooltip>
                      <div class="bed-no">{{ bed.bedNo }}</div>
                    </div>
                  </template>
                </div>
                <div class="bed-row">
                  <template v-for="(bed, bedIdx) in room.beds.slice(2, 4)" :key="bed.bedNo">
                    <div :class="['bed-status-container', `bed-${bed.status}`]">
                      <el-tooltip placement="top" :effect="tooltipEffect" popper-class="bed-tooltip">
                        <template #content>
                          <div v-if="room.details && room.details[bedIdx + 2]" class="customer-info-card">
                            <div class="customer-info-title">客户信息</div>
                            <div class="info-row"><span class="label">姓名：</span><span class="value">{{ room.details[bedIdx + 2].customerName }}</span></div>
                            <div class="info-row"><span class="label">性别：</span><span class="value">{{ room.details[bedIdx + 2].gender }}</span></div>
                            <div class="info-row"><span class="label">年龄：</span><span class="value">{{ room.details[bedIdx + 2].age }}</span></div>
                            <div class="info-row"><span class="label">入住时间：</span><span class="value">{{ room.details[bedIdx + 2].usageStartDate }}</span></div>
                            <div class="info-row"><span class="label">到期时间：</span><span class="value">{{ room.details[bedIdx + 2].usageEndDate }}</span></div>
                          </div>
                          <div v-else class="status-text">
                            {{ bedStatusText(bed.status) }}
                          </div>
                        </template>
                        <img :src="getBedIcon(bed.status)" class="bed-icon" />
                      </el-tooltip>
                      <div class="bed-no">{{ bed.bedNo }}</div>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </div>

            <!-- 两排房间之间的走廊 -->
            <div v-if="rowIdx < roomRows.length - 1" class="corridor-bar">
              <span class="passage-label">走 廊</span>
            </div>
          </template>
        </div>

        <!-- 东门：整层最右侧竖条 -->
        <div class="gate-side gate-east">
          <span class="gate-label-vertical">东门</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import freeBedSvg from '@/assets/free_bed.svg'
import outBedSvg from '@/assets/out_bed.svg'
import totalBedSvg from '@/assets/total_bed.svg'
import usedBedSvg from '@/assets/used_bed.svg'
import { getBedMap, getFloorList } from '@/api/bed'
import { BUILDING_OPTIONS, DEFAULT_BUILDING } from '@/utils/building'
import { useSettingStore } from '@/stores/setting'
import { ThemeMode } from '@/config/setting'
import { usePreferredDark } from '@vueuse/core'

/** 每排房间数量，与床位示意图布局一致 */
const ROOMS_PER_ROW = 5

const settingStore = useSettingStore()
const prefersDark = usePreferredDark()

const isDark = computed(() => {
  const mode = settingStore.systemThemeMode
  if (mode === ThemeMode.DARK) return true
  if (mode === ThemeMode.LIGHT) return false
  return prefersDark.value
})

const tooltipEffect = computed(() => (isDark.value ? 'dark' : 'light'))

const buildingOptions = BUILDING_OPTIONS
const selectedBuilding = ref(DEFAULT_BUILDING)

const floorList = ref([])
const selectedFloor = ref('')
const roomList = ref([])

const bedStats = reactive({ total: 0, free: 0, used: 0, out: 0 })
const maxBedCount = ref(0)

/**
 * 将房间按行分组，用于两排房间 + 中间走廊布局。
 *
 * @returns 分组后的房间列表
 */
const roomRows = computed(() => {
  const rows = []
  let currentRow = []
  for (let i = 0; i < roomList.value.length; i++) {
    currentRow.push(roomList.value[i])
    if (currentRow.length === ROOMS_PER_ROW) {
      rows.push(currentRow)
      currentRow = []
    }
  }
  if (currentRow.length > 0) rows.push(currentRow)
  return rows
})

/** 当前楼层单行最多房间数，用于计算整层宽度与东门位置 */
const maxRoomsInFloor = computed(() => {
  if (!roomRows.value.length) return ROOMS_PER_ROW
  return Math.max(...roomRows.value.map((row) => row.length))
})

/** 西门 + 东门 + 间距占用宽度 */
const FLOOR_GATE_GUTTER = 104

/**
 * 整层房间区域宽度：满排时撑满，未满排时按 5 间基准等比收缩，东门紧贴末列。
 *
 * @returns 楼层布局容器样式
 */
const floorLayoutStyle = computed(() => {
  const ratio = maxRoomsInFloor.value / ROOMS_PER_ROW
  if (ratio >= 1) {
    return { flex: '1 1 0', minWidth: '0', width: 'auto' }
  }
  return {
    flex: '0 0 auto',
    width: `calc((100% - ${FLOOR_GATE_GUTTER}px) * ${ratio})`,
  }
})

/**
 * 每行网格宽度占满层的比例，保证单个房间宽度与满排 5 间时一致。
 *
 * @param row 当前行房间列表
 * @returns 网格布局样式
 */
function getRoomGridStyle(row) {
  const count = row.length
  const max = maxRoomsInFloor.value
  return {
    gridTemplateColumns: `repeat(${count}, 1fr)`,
    width: `${(count / max) * 100}%`,
  }
}

function bedStatusText(status) {
  switch (status) {
    case 'free': return '空闲'
    case 'used': return '有人'
    case 'out': return '外出'
    default: return ''
  }
}

function getBedIcon(status) {
  switch (status) {
    case 'free': return freeBedSvg
    case 'used': return usedBedSvg
    case 'out': return outBedSvg
    default: return ''
  }
}

function fetchRoomList() {
  getBedMap({ floor: selectedFloor.value, building: selectedBuilding.value }).then((res) => {
    roomList.value = res || []
    maxBedCount.value = Math.max(...roomList.value.map((r) => r.beds.length), 0)
    updateBedStats()
  })
}

function updateBedStats() {
  let total = 0, free = 0, used = 0, out = 0
  roomList.value.forEach((room) => {
    if (room.beds && Array.isArray(room.beds)) {
      room.beds.forEach((bed) => {
        total++
        if (bed.status === 'free') free++
        else if (bed.status === 'used') used++
        else if (bed.status === 'out') out++
      })
    }
  })
  bedStats.total = total
  bedStats.free = free
  bedStats.used = used
  bedStats.out = out
}

function onBuildingChange() {
  selectedFloor.value = ''
  floorList.value = []
  roomList.value = []
  fetchFloorList()
}

function fetchFloorList() {
  getFloorList(selectedBuilding.value).then((res) => {
    floorList.value = res || []
    if (floorList.value.length > 0) {
      selectedFloor.value = floorList.value[0]
      fetchRoomList()
    }
  })
}

onMounted(() => { fetchFloorList() })
watch(roomList, () => { updateBedStats() }, { deep: true })
</script>

<style scoped>
.bed-map-page {
  color: var(--ui-gray-700);
  padding: 4px;
}

.bed-map-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding: 14px 18px;
  border-radius: 10px;
  background: var(--default-box-color);
  border: 1px solid var(--default-border);
}

.toolbar-left { display: flex; align-items: center; gap: 12px; }
.toolbar-right { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }

.stat-item {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  transition: all 0.2s ease;
}

.stat-total { background: var(--art-gray-100); color: var(--ui-gray-700); }
.stat-free { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
.stat-used { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
.stat-out { background: rgba(249, 115, 22, 0.1); color: #f97316; }

.icon-bed { width: 18px; height: 18px; vertical-align: middle; margin-right: 6px; }

.room-grid-wrapper {
  overflow-y: auto;
  height: calc(100vh - 200px);
  border-radius: 12px;
  border: 1px solid var(--default-border);
  background: var(--default-box-color);
  padding: 20px;
}

.room-grid-wrapper::-webkit-scrollbar { width: 6px; }
.room-grid-wrapper::-webkit-scrollbar-track { background: transparent; }
.room-grid-wrapper::-webkit-scrollbar-thumb { background: var(--art-gray-300); border-radius: 3px; }

.floor-shell {
  display: flex;
  align-items: stretch;
  gap: 12px;
  width: 100%;
  min-height: 200px;
}

.gate-side {
  flex-shrink: 0;
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid rgba(99, 102, 241, 0.28);
  background: rgba(99, 102, 241, 0.06);
}

.gate-label-vertical {
  writing-mode: vertical-rl;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 6px;
  color: #6366f1;
  white-space: nowrap;
}

.floor-layout {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.room-grid {
  display: grid;
  gap: 16px;
}

.corridor-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 12px 0;
  min-height: 48px;
  border-radius: 8px;
  border: 1px dashed var(--default-border);
  background: var(--art-gray-100);
  background-image: repeating-linear-gradient(
    90deg,
    transparent 0,
    transparent 14px,
    var(--default-border) 14px,
    var(--default-border) 15px
  );
}

.passage-label {
  padding: 4px 16px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 6px;
  color: var(--ui-gray-500);
  background: var(--default-box-color);
  border: 1px solid var(--default-border);
}

.room-card {
  border-radius: 10px;
  border: 1px solid var(--default-border);
  background: var(--default-box-color);
  overflow: hidden;
  transition: all 0.25s ease;
}

.room-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: #3b82f6;
}

.room-title {
  padding: 10px 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--ui-gray-700);
  background: var(--art-gray-100);
  border-bottom: 1px solid var(--default-border);
  text-align: center;
}

.beds-wrapper { display: flex; flex-direction: column; gap: 8px; padding: 16px 12px; }
.bed-row { display: flex; flex-direction: row; gap: 8px; justify-content: center; }

.bed-status-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s ease;
  cursor: pointer;
  min-width: 48px;
}

.bed-status-container:hover { transform: scale(1.08); }

.bed-free { background: rgba(34, 197, 94, 0.08); border: 1px solid rgba(34, 197, 94, 0.2); }
.bed-used { background: rgba(59, 130, 246, 0.08); border: 1px solid rgba(59, 130, 246, 0.2); }
.bed-out { background: rgba(249, 115, 22, 0.08); border: 1px solid rgba(249, 115, 22, 0.2); }

.bed-icon { width: 32px; height: 32px; margin-bottom: 4px; }
.bed-no { font-size: 12px; color: var(--ui-gray-600); font-weight: 500; }
</style>

<style>
.bed-tooltip .customer-info-card {
  font-size: 14px;
  padding: 14px 18px;
  line-height: 1.8;
  min-width: 180px;
  color: var(--ui-gray-800);
}

.bed-tooltip .customer-info-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--default-border);
  color: var(--ui-gray-800);
}

.bed-tooltip .info-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.bed-tooltip .label {
  color: var(--ui-gray-500);
}

.bed-tooltip .value {
  font-weight: 500;
  color: var(--ui-gray-800);
}

.bed-tooltip .status-text {
  padding: 4px 2px;
  font-weight: 500;
  color: var(--ui-gray-800);
}
</style>
