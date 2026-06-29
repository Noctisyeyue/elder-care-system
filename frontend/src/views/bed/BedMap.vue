<template>
  <div>
    <!-- 楼层选择和统计 -->
    <div style="display: flex; align-items: center; margin-bottom: 16px">
      <el-select
        v-model="selectedFloor"
        @change="fetchRoomList"
        style="width: 120px; margin-right: 20px"
      >
        <el-option v-for="floor in floorList" :key="floor" :label="floor" :value="floor" />
      </el-select>
      <span style="margin-right: 20px">
        <img :src="totalBedSvg" alt="Total Beds" class="icon-bed" /> 总床位：{{ bedStats.total }}
      </span>
      <span style="margin-right: 20px">
        <img :src="freeBedSvg" alt="Free Beds" class="icon-bed" /> 空闲：{{ bedStats.free }}
      </span>
      <span style="margin-right: 20px">
        <img :src="usedBedSvg" alt="Used Beds" class="icon-bed" /> 有人：{{ bedStats.used }}
      </span>
      <span>
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
                        <el-tooltip placement="top" effect="light">
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
                        <el-tooltip placement="top" effect="light">
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
// 导入 SVG 文件
import freeBedSvg from '@/assets/free_bed.svg'
import outBedSvg from '@/assets/out_bed.svg'
import totalBedSvg from '@/assets/total_bed.svg'
import usedBedSvg from '@/assets/used_bed.svg'
import { getBedMap, getFloorList } from '@/api/bed'
// 楼层列表
const floorList = ref([])
const selectedFloor = ref('')

// 房间及床位数据
const roomList = ref([])

// 统计数据
const bedStats = reactive({
  total: 0,
  free: 0,
  used: 0,
  out: 0,
})

// 最大床位数（用于动态生成表头）
const maxBedCount = ref(0)

// 处理分组，每行最多5个房间，基于 roomList
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

// 床位状态文字
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

// 根据床位状态获取对应的SVG图标
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

// 获取房间及床位信息
// 后端data需要返回：
// {
//   list: Array<{
//     roomNumber: string;
//     beds: Array<{
//       bedNo: string;
//       status: 'free' | 'used' | 'out';
//     }>;
//   }>;
// }
function fetchRoomList() {
  getBedMap({
    floor: selectedFloor.value,
  }).then((res) => {
    roomList.value = res || []
    // 统计最大床位数
    maxBedCount.value = Math.max(...roomList.value.map((r) => r.beds.length), 0)
    updateBedStats()
  })
}

// 统计床位信息，基于 roomList
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

function fetchFloorList() {
  getFloorList().then((res) => {
    floorList.value = res || []
    if (floorList.value.length > 0) {
      selectedFloor.value = floorList.value[0]
      fetchRoomList()
    }
  })
}

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
.icon-bed {
  width: 20px; /* 调整图标大小 */
  height: 20px;
  vertical-align: middle; /* 使图标与文字对齐 */
  margin-right: 4px;
}

.bed-status-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4px;
  border-radius: 4px;
  font-size: 14px;
}

.bed-icon {
  width: 30px; /* 床位图标大小 */
  height: 30px;
  margin-bottom: 4px; /* 图标和文字之间的间距 */
}

.custom-table-wrapper {
  overflow-y: auto;
  margin-top: 16px;
  height: calc(100vh - 200px);
}
.custom-table {
  border-collapse: collapse;
  width: 100%;
  background: #fff;
}
.custom-table th,
.custom-table td {
  border: 1px solid #ccc;
  text-align: center;
  padding: 8px;
  min-width: 80px;
}
.custom-table th {
  background: #f8f8f8;
  font-weight: bold;
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

/* 客户信息卡片样式 */
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
