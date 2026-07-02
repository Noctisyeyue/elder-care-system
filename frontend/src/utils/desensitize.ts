/**
 * 敏感数据展示脱敏工具。
 *
 * <p>仅在「展示层」打掩码，数据库仍存储完整值（用于办理手续等场景）。
 * 与后端 SensitiveDataUtil（审计日志脱敏）配合，形成"存储完整 + 展示脱敏 + 日志脱敏"三层保护。
 * 参考 GB/T 35273 个人信息安全规范。
 */

/**
 * 身份证号脱敏：保留前 6 位（地区码）和后 4 位，中间打 8 个 *。
 * 例：450323222202222155 → 450323********2155
 *
 * @param id 身份证号
 * @returns 脱敏后的字符串，长度不足时原样返回
 */
export function maskIdNumber(id?: string | null): string {
  if (!id) return ''
  if (id.length < 11) return id
  return id.substring(0, 6) + '********' + id.substring(id.length - 4)
}

/**
 * 手机号脱敏：保留前 3 位和后 4 位，中间打 4 个 *。
 * 例：13800138000 → 138****8000
 *
 * @param phone 手机号
 * @returns 脱敏后的字符串，长度不足时原样返回
 */
export function maskPhone(phone?: string | null): string {
  if (!phone) return ''
  if (phone.length < 7) return phone
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
}
