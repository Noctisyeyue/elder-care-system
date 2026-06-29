import { marked } from 'marked'

// 配置marked选项
marked.setOptions({
  breaks: true, // 支持换行
  gfm: true, // 支持GitHub风格的Markdown
})

/**
 * 预处理Markdown文本，修复常见格式问题
 * @param text 原始文本
 * @returns 预处理后的文本
 */
function preprocessMarkdown(text: string): string {
  if (!text) return text

  return (
    text
      // 修复编号格式：.1 -> 1.
      .replace(/^\.(\d+)/gm, '$1.')

      // 移除多余的分隔符：###--- -> ###
      .replace(/###-+/g, '###')

      // 修复标题格式：###标题 -> ### 标题
      .replace(/^(#{1,6})([^#\s])/gm, '$1 $2')

      // 修复多余的星号：**** -> **
      .replace(/\*{3,}/g, '**')

      // 修复列表格式：- 项目 -> - 项目
      .replace(/^-\s*([^-\s])/gm, '- $1')

      // 修复括号问题：可选个（） -> 可选个
      .replace(/（\s*）/g, '')
      .replace(/\(\s*\)/g, '')

      // 修复冒号后的格式：材料****： -> 材料：
      .replace(/([^：]*)：\*+/g, '$1：')

      // 修复步骤标题：步骤： -> ### 步骤
      .replace(/^步骤：/gm, '### 步骤')

      // 修复材料标题：材料： -> ### 材料
      .replace(/^材料：/gm, '### 材料')

      // 修复多余的标点符号
      .replace(/[。，、]{2,}/g, '，')
  )
}

/**
 * 渲染Markdown文本
 * @param text Markdown格式的文本
 * @returns 渲染后的HTML字符串
 */
export function renderMarkdown(text: string): string {
  if (!text) return text

  try {
    // 先预处理文本
    const processedText = preprocessMarkdown(text)

    const result = marked(processedText)
    // 处理可能的Promise返回值
    if (typeof result === 'string') {
      return result
    } else {
      // 如果是Promise，返回原始文本
      console.warn('Markdown渲染返回了Promise，使用原始文本')
      return text
    }
  } catch (error) {
    console.warn('Markdown渲染错误:', error)
    return text
  }
}

/**
 * 检查文本是否包含Markdown标记
 * @param text 要检查的文本
 * @returns 是否包含Markdown标记
 */
export function hasMarkdown(text: string): boolean {
  if (!text) return false

  const markdownPatterns = [
    /\*\*.*?\*\*/, // 粗体
    /\*.*?\*/, // 斜体
    /#+\s/, // 标题
    /- /, // 无序列表
    /\d+\. /, // 有序列表
    /`.*?`/, // 行内代码
    /```[\s\S]*?```/, // 代码块
    /\[.*?\]\(.*?\)/, // 链接
    /!\[.*?\]\(.*?\)/, // 图片
  ]

  return markdownPatterns.some((pattern) => pattern.test(text))
}
