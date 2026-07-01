<!-- 父菜单--床位管理 -->
<template>
  <router-view />
</template>

<script setup>
/**
 * 床位管理布局页。
 */
</script>

<style scoped></style>

<!-- 模拟数据，测试用 -->
<!-- <script setup>
import { ref, reactive, onMounted } from 'vue'
// 导入 SVG 文件
import freeBedSvg from '@/assets/free_bed.svg'
import outBedSvg from '@/assets/out_bed.svg'
import totalBedSvg from '@/assets/total_bed.svg'
import usedBedSvg from '@/assets/used_bed.svg'

// 楼层列表
const floorList = ref(['一层', '二层', '三层'])
const selectedFloor = ref('一层')

// 房间及床位数据（模拟数据）
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

// 模拟数据
const mockData = {
  一层: [
    {
      roomNumber: '1001',
      beds: [
        { bedNumber: '1001-1', status: 'used' },
        { bedNumber: '1001-2', status: 'out' },
      ],
    },
    {
      roomNumber: '1002',
      beds: [
        { bedNumber: '1002-1', status: 'free' },
        { bedNumber: '1002-2', status: 'used' },
      ],
    },
    {
      roomNumber: '1003',
      beds: [{ bedNumber: '1003-1', status: 'free' }],
    },
    {
      roomNumber: '1004',
      beds: [
        { bedNumber: '1004-1', status: 'out' },
        { bedNumber: '1004-2', status: 'free' },
      ],
    },
  ],
  二层: [
    {
      roomNumber: '2001',
      beds: [
        { bedNumber: '2001-1', status: 'used' },
        { bedNumber: '2001-2', status: 'used' },
      ],
    },
    {
      roomNumber: '2002',
      beds: [{ bedNumber: '2002-1', status: 'free' }],
    },
  ],
  三层: [
    {
      roomNumber: '3001',
      beds: [{ bedNumber: '3001-1', status: 'out' }],
    },
  ],
}

// 获取房间及床位信息（模拟）
function fetchRoomList() {
  roomList.value = mockData[selectedFloor.value] || []
  // 统计最大床位数
  maxBedCount.value = Math.max(...roomList.value.map((r) => r.beds.length), 0)
  // 统计床位状态
  let total = 0,
    free = 0,
    used = 0,
    out = 0
  roomList.value.forEach((room) => {
    room.beds.forEach((bed) => {
      total++
      if (bed.status === 'free') free++
      else if (bed.status === 'used') used++
      else if (bed.status === 'out') out++
    })
  })
  bedStats.total = total
  bedStats.free = free
  bedStats.used = used
  bedStats.out = out
}

onMounted(() => {
  fetchRoomList()
})
</script> -->
