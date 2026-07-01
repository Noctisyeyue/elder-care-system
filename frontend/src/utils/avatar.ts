const avatarPalette = [
  '#0F766E',
  '#2563EB',
  '#7C3AED',
  '#C2410C',
  '#B45309',
  '#047857',
  '#BE123C',
  '#4338CA',
]

const legacyDefaultAvatars = [
  'https://img95.699pic.com/element/40112/2503.png_300.png',
]

/**
 * 根据用户名生成稳定的文字头像。
 * 颜色在不同用户之间看起来是随机的，但同一个用户每次刷新都会保持一致。
 */
export function getTextAvatar(name?: string) {
  const displayName = (name || '').trim()
  const initial = displayName ? displayName.slice(0, 1).toUpperCase() : '用'
  const seed = displayName || initial
  let hash = 0

  for (let i = 0; i < seed.length; i++) {
    hash = seed.charCodeAt(i) + ((hash << 5) - hash)
  }

  return {
    initial,
    style: {
      backgroundColor: avatarPalette[Math.abs(hash) % avatarPalette.length],
      color: '#FFFFFF',
    },
  }
}

/**
 * 规范化头像地址。
 * 后端旧逻辑会在没有头像时返回默认图片地址，前端需要把它当成“未上传头像”处理。
 */
export function normalizeAvatarUrl(url?: string | null) {
  const avatarUrl = (url || '').trim()
  if (!avatarUrl || ['null', 'undefined'].includes(avatarUrl.toLowerCase())) {
    return ''
  }
  if (legacyDefaultAvatars.includes(avatarUrl)) {
    return ''
  }
  return avatarUrl
}
