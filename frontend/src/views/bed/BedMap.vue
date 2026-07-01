<template>
  <div class="bed-map-page">
    <!-- 楼层选择和统计 -->
    <div class="bed-map-toolbar">
      <el-select
        v-model="selectedBuilding"
        @change="onBuildingChange"
        style="width: 120px"
        placeholder="选择楼栋"
      >
        <el-option
          v-for="item in buildingOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select
        v-model="selectedFloor"
        @change="fetchRoomList"
        style="width: 120px"
      >
        <el-option v-for="floor in floorList" :key="floor" :label="floor" :value="floor" />
      </el-select>
      <span class="stat-item">
        <img :src="totalBedSvg" alt="Total Beds" class="icon-bed" /> 总床位：{{ bedStats.total }}
      </span>
      <span class="stat-item">
        <img :src="freeBedSvg" alt="Free Beds" class="icon-bed" /> 空闲：{{ bedStats.free }}
      </span>
      <span class="stat-item">
        <img :src="usedBedSvg" alt="Used Beds" class="icon-bed" /> 有人：{{ bedStats.used }}
      </span>
      <span class="stat-item">
        <img :src="outBedSvg" alt="Out Beds" class="icon-bed" /> 外出：{{ bedStats.out }}
      </span>
    </div>

    <!-- 房间及床位自定义表格 -->
    <div class="custom-table-wrapper">
      <table class="custom-table">
        <template v-for="(row, rowIdx) in tableRows" :key="rowIdx">
          <!-- 表头行 -->
          <tr>
            <template v-for="cell in row" :key="cell.roomNumber">
              <th>{{ cell.roomNumber }}</th>
            </template>
          </tr>
          <!-- 床位行 -->
          <tr>
            <template v-for="cell in row" :key="cell.roomNumber">
              <td>
                <div class="beds-wrapper">
                  <div class="bed-row">
                    <template v-for="(bed, bedIdx) in cell.beds.slice(0, 2)" :key="bed.bedNo">
                      <div :class="['bed-status-container', bed.status]">
                        <el-tooltip placement="top" :effect="tooltipEffect">
                          <template #content>
                            <div v-if="cell.details && cell.details[bedIdx]" class="customer-info-card">
                              <div class="customer-info-title">客户信息</div>
                              <div>姓名：{{ cell.details[bedIdx].customerName }}</div>
                              <div>性别：{{ cell.details[bedIdx].gender }}</div>
                              <div>年龄：{{ cell.details[bedIdx].age }}</div>
                              <div>入住时间：{{ cell.details[bedIdx].usageStartDate }}</div>
                              <div>到期时间：{{ cell.details[bedIdx].usageEndDate }}</div>
                            </div>
                            <div v-else>
                              <span>{{ bedStatusText(bed.status) }}</span>
                            </div>
                          </template>
                          <img :src="getBedIcon(bed.status)" class="bed-icon" />
                        </el-tooltip>
                        <div>{{ bed.bedNo }}</div>
                      </div>
                    </template>
                  </div>
                  <div class="bed-row">
                    <template v-for="(bed, bedIdx) in cell.beds.slice(2, 4)" :key="bed.bedNo">
                      <div :class="['bed-status-container', bed.status]">
                        <el-tooltip placement="top" :effect="tooltipEffect">
                          <template #content>
                            <div v-if="cell.details && cell.details[bedIdx + 2]" class="customer-info-card">
                              <div class="customer-info-title">客户信息</div>
                              <div>姓名：{{ cell.details[bedIdx + 2].customerName }}</div>
                              <div>性别：{{ cell.details[bedIdx + 2].gender }}</div>
                              <div>年龄：{{ cell.details[bedIdx + 2].age }}</div>
                              <div>入住时间：{{ cell.details[bedIdx + 2].usageStartDate }}</div>
                              <div>到期时间：{{ cell.details[bedIdx + 2].usageEndDate }}</div>
                            </div>
                            <div v-else>
                              <span>{{ bedStatusText(bed.status) }}</span>
                            </div>
                          </template>
                          <img :src="getBedIcon(bed.status)" class="bed-icon" />
                        </el-tooltip>
                        <div>{{ bed.bedNo }}</div>
                      </div>
                    </template>
                  </div>
                </div>
              </td>
            </template>
          </tr>
        </template>
      </table>
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

/** 楼层列表。 */
const floorList = ref([])
const selectedFloor = ref('')

/** 房间及床位数据。 */
const roomList = ref([])

/** 床位统计数据。 */
const bedStats = reactive({
  total: 0,
  free: 0,
  used: 0,
  out: 0,
})

/** 最大床位数。 */
const maxBedCount = ref(0)

/**
 * 将房间按行分组。
 *
 * @returns 分组后的房间列表
 */
const tableRows = computed(() => {
  const rows = []
  let currentRow = []
  for (let i = 0; i < roomList.value.length; i++) {
    currentRow.push(roomList.value[i])
    if (currentRow.length === 5) {
      rows.push(currentRow)
      currentRow = []
    }
  }
  if (currentRow.length > 0) rows.push(currentRow)
  return rows
})

/**
 * 获取床位状态文字。
 *
 * @param status 床位状态
 * @returns 状态文字
 */
function bedStatusText(status) {
  switch (status) {
    case 'free':
      return '空闲'
    case 'used':
      return '有人'
    case 'out':
      return '外出'
    default:
      return ''
  }
}

/**
 * 获取床位状态图标。
 *
 * @param status 床位状态
 * @returns 图标地址
 */
function getBedIcon(status) {
  switch (status) {
    case 'free':
      return freeBedSvg
    case 'used':
      return usedBedSvg
    case 'out':
      return outBedSvg
    default:
      return ''
  }
}

/**
 * 获取房间及床位信息。
 *
 * @returns 无返回值
 */
function fetchRoomList() {
  getBedMap({
    floor: selectedFloor.value,
    building: selectedBuilding.value,
  }).then((res) => {
    roomList.value = res || []
    maxBedCount.value = Math.max(...roomList.value.map((r) => r.beds.length), 0)
    updateBedStats()
  })
}

/**
 * 统计床位信息。
 *
 * @returns 无返回值
 */
function updateBedStats() {
  let total = 0,
    free = 0,
    used = 0,
    out = 0
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

/**
 * 查询楼层列表。
 *
 * @returns 无返回值
 */
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

/**
 * 组件挂载时初始化楼层和床位数据。
 *
 * @returns 无返回值
 */
onMounted(() => {
  fetchFloorList()
})

watch(
  roomList,
  () => {
    updateBedStats()
  },
  { deep: true },
)
</script>

<style scoped>
.bed-map-page {
  color: var(--ui-gray-700);
}

.bed-map-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px 20px;
  margin-bottom: 16px;
  font-size: 14px;
  color: var(--ui-gray-700);
}

.stat-item {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
}

.icon-bed {
  width: 20px;
  height: 20px;
  vertical-align: middle;
  margin-right: 4px;
}

.bed-status-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--ui-gray-600);
  transition: background-color 0.2s;
}

.bed-status-container:hover {
  background-color: var(--ui-hover-color);
}

.bed-status-container > div:last-child {
  font-size: 12px;
  color: var(--ui-gray-500);
  margin-top: 2px;
  font-weight: 500;
}

.bed-icon {
  width: 30px;
  height: 30px;
  margin-bottom: 4px;
}

.custom-table-wrapper {
  overflow-y: auto;
  margin-top: 16px;
  height: calc(100vh - 200px);
  border-radius: 10px;
  border: 1px solid var(--default-border);
  background: var(--default-box-color);
}

.custom-table {
  border-collapse: collapse;
  width: 100%;
  background: transparent;
}

.custom-table th,
.custom-table td {
  border: 1px solid var(--default-border);
  text-align: center;
  padding: 8px;
  min-width: 80px;
}

.custom-table th {
  background: var(--ui-gray-100);
  color: var(--ui-gray-700);
  font-weight: 600;
  font-size: 14px;
  padding: 10px 8px;
}

.custom-table td {
  background: var(--default-box-color);
}

.beds-wrapper {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.bed-row {
  display: flex;
  flex-direction: row;
  gap: 2px;
  justify-content: center;
}

.customer-info-card {
  font-size: 15px;
  padding: 18px 24px;
  line-height: 2;
  min-width: 150px;
}

.customer-info-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}
</style>
