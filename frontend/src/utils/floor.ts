/** 中文数字与楼层序号映射 */
const CN_FLOOR_NUM: Record<string, number> = {
  一: 1,
  二: 2,
  三: 3,
  四: 4,
  五: 5,
  六: 6,
  七: 7,
  八: 8,
  九: 9,
  十: 10,
}

/**
 * 解析楼层名称对应的排序序号。
 *
 * @param floor 楼层名称
 * @returns 排序序号，无法解析时返回最大值
 */
function resolveFloorOrder(floor: string): number {
  if (!floor) return Number.MAX_SAFE_INTEGER

  const name = floor.endsWith('层') ? floor.slice(0, -1) : floor

  if (/^\d+$/.test(name)) {
    return parseInt(name, 10)
  }

  if (name.length === 1 && CN_FLOOR_NUM[name] != null) {
    return CN_FLOOR_NUM[name]
  }

  if (name.startsWith('十') && name.length === 2) {
    return 10 + (CN_FLOOR_NUM[name[1]] ?? 0)
  }

  if (name.endsWith('十') && name.length === 2) {
    return (CN_FLOOR_NUM[name[0]] ?? 0) * 10
  }

  return Number.MAX_SAFE_INTEGER
}

/**
 * 按楼层实际高低顺序排序。
 *
 * @param floors 楼层名称列表
 * @returns 排序后的楼层列表
 */
export function sortFloorList(floors: string[]): string[] {
  return [...floors].sort((a, b) => resolveFloorOrder(a) - resolveFloorOrder(b))
}
